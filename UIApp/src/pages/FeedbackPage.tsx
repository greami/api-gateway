import { useEffect, useMemo, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import {
  createFeedbackEvent,
  createFeedbackOpera,
  deleteFeedbackEvent,
  deleteFeedbackOpera,
  getBookingsByUserId,
  getEvents,
  getEventsByName,
  getFeedbacksByUserId,
  getFeedbacksOpereByUserId,
  getOpereByEvento,
  getOpereByNome,
  updateFeedbackEvent,
  updateFeedbackOpera,
} from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type {
  Evento,
  EventoOpera,
  FeedbackEvento,
  FeedbackOpera,
  Opera,
  Prenotazione,
} from '../lib/types';
import { useAuth } from '../state/AuthContext';
import { appConfig } from '../config/appConfig';
import { EventCard } from '../components/event/EventCard';
import { OperaCard } from '../components/opera/OperaCard';
import { DeleteConfirmModal } from '../components/forms/DeleteConfirmModal';
import { FeedbackModal } from '../components/forms/FeedbackModal';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { NotFoundPage } from './NotFoundPage';
import { ResultModal } from '../components/forms/ResultModal';
import { AdminFeedbackPage } from './admin/AdminFeedbackPage';

type FeedbackDraft = { voto: number; note: string };
type FeedbackResource = { kind: 'evento'; item: Evento } | { kind: 'opera'; item: Opera };

type FeedbackFormModalState =
  | {
      resource: FeedbackResource;
      mode: 'create' | 'edit';
      feedback?: FeedbackEvento | FeedbackOpera;
      draft: FeedbackDraft;
    }
  | null;

type DeleteModalState =
  | {
      resource: FeedbackResource;
      feedback: FeedbackEvento | FeedbackOpera;
    }
  | null;

type ResultModalState =
  | {
      title: string;
      message: string;
      onOk: () => void;
    }
  | null;

type SelectionModalState = {
  kind: 'evento' | 'opera';
  resources: FeedbackResource[];
} | null;

function ResourceSelectionModal({
  title,
  resources,
  selectedId,
  onSelect,
  onClose,
  onConfirm,
}: {
  title: string;
  resources: FeedbackResource[];
  selectedId: number | null;
  onSelect: (resource: FeedbackResource) => void;
  onClose: () => void;
  onConfirm: () => void;
}) {
  return (
    <Modal
      title={title}
      onClose={onClose}
      scrollable
      actions={
        <>
          <Button variant="secondary" type="button" onClick={onClose}>
            {strings.modal.cancel}
          </Button>
          <Button type="button" disabled={selectedId === null} onClick={onConfirm}>
            {strings.modal.confirm}
          </Button>
        </>
      }
    >
      {resources.length === 0 ? (
        <p className="text-sm text-slate-300">{strings.feedback.noAvailableResources}</p>
      ) : (
        <div className="space-y-2">
          {resources.map((resource) => {
            const id = resource.kind === 'evento' ? resource.item.idEvento : resource.item.idOpera;
            return (
              <label
                key={`${resource.kind}-${id}`}
                className={`flex w-full items-center justify-between rounded-lg border p-3 text-left transition ${
                  selectedId === id ? 'border-cyan-300 bg-cyan-400/10' : 'border-white/10 bg-white/5 hover:bg-white/10'
                }`}
              >
                <span className="flex items-center gap-3 font-medium text-white">
                  <input
                    type="radio"
                    name="feedback-resource"
                    checked={selectedId === id}
                    onChange={() => onSelect(resource)}
                  />
                  {resource.item.nome}
                </span>
                <span className="text-xs text-slate-400">{strings.feedback.selectResource}</span>
              </label>
            );
          })}
        </div>
      )}
    </Modal>
  );
}

export function FeedbackPage() {
  const { user } = useAuth();
  return user?.roles.includes('ROLE_ADMIN') ? <AdminFeedbackPage /> : <UserFeedbackPage />;
}

function UserFeedbackPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<'events' | 'works'>('events');
  const [eventSortOrder, setEventSortOrder] = useState<'newest' | 'oldest' | 'nameAsc' | 'nameDesc'>('newest');
  const [workSortOrder, setWorkSortOrder] = useState<'newest' | 'oldest' | 'nameAsc' | 'nameDesc'>('newest');
  const [eventNameFilter, setEventNameFilter] = useState('');
  const [workNameFilter, setWorkNameFilter] = useState('');
  const [eventSearchResults, setEventSearchResults] = useState<Evento[] | null>(null);
  const [workSearchResults, setWorkSearchResults] = useState<Opera[] | null>(null);
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [eventFeedbacks, setEventFeedbacks] = useState<FeedbackEvento[]>([]);
  const [works, setWorks] = useState<Opera[]>([]);
  const [eventWorks, setEventWorks] = useState<EventoOpera[]>([]);
  const [workFeedbacks, setWorkFeedbacks] = useState<FeedbackOpera[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [formModal, setFormModal] = useState<FeedbackFormModalState>(null);
  const [deleteModal, setDeleteModal] = useState<DeleteModalState>(null);
  const [resultModal, setResultModal] = useState<ResultModalState>(null);
  const [selectionModal, setSelectionModal] = useState<SelectionModalState>(null);
  const [busy, setBusy] = useState(false);
  const featureEnabled = appConfig.featureFlags.feedback;
  const buildErrorMessage = (caughtError: unknown) =>
    caughtError instanceof Error ? caughtError.message : strings.errors.unknown;

  const pastEventsBase = useMemo(
    () => {
      const matchingEvents = bookings
        .map((booking) => events.find((event) => event.idEvento === booking.idEvento))
        .filter((event): event is Evento => Boolean(event))
        .filter((event) => !isFutureDate(event.data));
      return [...new Map(matchingEvents.map((event) => [event.idEvento, event])).values()];
    },
    [bookings, events],
  );

  const pastEventIds = useMemo(() => new Set(pastEventsBase.map((event) => event.idEvento)), [pastEventsBase]);

  const pastWorksBase = useMemo(() => {
    const associatedWorkIds = new Set(
      eventWorks
        .filter((association) => pastEventIds.has(association.idEvento))
        .map((association) => association.idOpera),
    );
    return works
      .filter((work) => associatedWorkIds.has(work.idOpera))
      .sort((a, b) => {
        const aDate = Math.max(
          ...eventWorks.filter((association) => association.idOpera === a.idOpera).map((association) => {
            const event = events.find((item) => item.idEvento === association.idEvento);
            return event ? new Date(event.data).getTime() : 0;
          }),
        );
        const bDate = Math.max(
          ...eventWorks.filter((association) => association.idOpera === b.idOpera).map((association) => {
            const event = events.find((item) => item.idEvento === association.idEvento);
            return event ? new Date(event.data).getTime() : 0;
          }),
        );
        return bDate - aDate;
      });
  }, [eventWorks, events, pastEventIds, works]);

  const pastEvents = useMemo(() => {
    const filtered = pastEventsBase.filter((event) =>
      eventSearchResults
        ? eventSearchResults.some((result) => result.idEvento === event.idEvento)
        : event.nome.toLocaleLowerCase().includes(eventNameFilter.trim().toLocaleLowerCase()),
    );
    return [...filtered].sort((a, b) => {
      if (eventSortOrder === 'nameAsc' || eventSortOrder === 'nameDesc') {
        const comparison = a.nome.localeCompare(b.nome, 'it');
        return eventSortOrder === 'nameAsc' ? comparison : -comparison;
      }
      const comparison = new Date(b.data).getTime() - new Date(a.data).getTime();
      return eventSortOrder === 'newest' ? comparison : -comparison;
    });
  }, [eventNameFilter, eventSearchResults, eventSortOrder, pastEventsBase]);

  const pastWorks = useMemo(() => {
    const filtered = pastWorksBase.filter((work) =>
      workSearchResults
        ? workSearchResults.some((result) => result.idOpera === work.idOpera)
        : work.nome.toLocaleLowerCase().includes(workNameFilter.trim().toLocaleLowerCase()),
    );
    return [...filtered].sort((a, b) => {
      if (workSortOrder === 'nameAsc' || workSortOrder === 'nameDesc') {
        const comparison = a.nome.localeCompare(b.nome, 'it');
        return workSortOrder === 'nameAsc' ? comparison : -comparison;
      }
      const aDate = Math.max(
        ...eventWorks.filter((association) => association.idOpera === a.idOpera).map((association) => {
          const event = events.find((item) => item.idEvento === association.idEvento);
          return event ? new Date(event.data).getTime() : 0;
        }),
      );
      const bDate = Math.max(
        ...eventWorks.filter((association) => association.idOpera === b.idOpera).map((association) => {
          const event = events.find((item) => item.idEvento === association.idEvento);
          return event ? new Date(event.data).getTime() : 0;
        }),
      );
      const comparison = bDate - aDate;
      return workSortOrder === 'newest' ? comparison : -comparison;
    });
  }, [eventWorks, events, pastWorksBase, workNameFilter, workSearchResults, workSortOrder]);

  const refreshFeedbackData = async (userId: number) => {
    const [userBookings, allEvents, userEventFeedbacks, userWorkFeedbacks] = await Promise.all([
      getBookingsByUserId(userId),
      getEvents(),
      getFeedbacksByUserId(userId),
      getFeedbacksOpereByUserId(userId),
    ]);
    const pastEventIdsForRefresh = new Set(
      userBookings
        .map((booking) => allEvents.find((event) => event.idEvento === booking.idEvento))
        .filter((event): event is Evento => Boolean(event))
        .filter((event) => !isFutureDate(event.data))
        .map((event) => event.idEvento),
    );
    const worksByEvent = await Promise.all(
      [...pastEventIdsForRefresh].map(async (idEvento) => ({
        idEvento,
        works: await getOpereByEvento(idEvento),
      })),
    );
    const associations = worksByEvent.flatMap(({ idEvento, works: eventWorksForId }) =>
      eventWorksForId.map((work) => ({ idEvento, idOpera: work.idOpera })),
    );
    const allWorks = [...new Map(worksByEvent.flatMap(({ works: eventWorksForId }) => eventWorksForId.map((work) => [work.idOpera, work]))).values()];

    setError(null);
    setBookings(userBookings);
    setEvents(allEvents);
    setEventFeedbacks(userEventFeedbacks);
    setWorks(allWorks);
    setEventWorks(associations);
    setWorkFeedbacks(userWorkFeedbacks);
  };

  useEffect(() => {
    if (!featureEnabled || !user) return;
    setLoading(true);
    refreshFeedbackData(user.idUtente)
      .catch((caughtError: unknown) => {
        if (caughtError instanceof ApiError && caughtError.status >= 500) {
          setFatalError(caughtError);
          return;
        }
        setError(caughtError instanceof Error ? caughtError.message : strings.errors.unknown);
      })
      .finally(() => setLoading(false));
  }, [featureEnabled, user]);

  useEffect(() => {
    const query = eventNameFilter.trim();
    if (!query) {
      setEventSearchResults(null);
      return;
    }
    let active = true;
    getEventsByName(query)
      .then((results) => {
        if (active) setEventSearchResults(results);
      })
      .catch((caughtError: unknown) => {
        if (active) setError(buildErrorMessage(caughtError));
      });
    return () => {
      active = false;
    };
  }, [eventNameFilter]);

  useEffect(() => {
    const query = workNameFilter.trim();
    if (!query) {
      setWorkSearchResults(null);
      return;
    }
    let active = true;
    getOpereByNome(query)
      .then((results) => {
        if (active) setWorkSearchResults(results);
      })
      .catch((caughtError: unknown) => {
        if (active) setError(buildErrorMessage(caughtError));
      });
    return () => {
      active = false;
    };
  }, [workNameFilter]);

  if (fatalError) return <GenericServerErrorPage error={fatalError} />;
  if (!featureEnabled) return <NotFoundPage />;

  const closeResultModal = () => setResultModal(null);

  const openCreateModal = (resource: FeedbackResource) => {
    setError(null);
    setDeleteModal(null);
    setFormModal({ resource, mode: 'create', draft: { voto: 5, note: '' } });
  };

  const openEditModal = (resource: FeedbackResource, feedback: FeedbackEvento | FeedbackOpera) => {
    setError(null);
    setDeleteModal(null);
    setFormModal({
      resource,
      mode: 'edit',
      feedback,
      draft: { voto: feedback.voto, note: feedback.note },
    });
  };

  const openDeleteModal = (resource: FeedbackResource, feedback: FeedbackEvento | FeedbackOpera) => {
    setError(null);
    setFormModal(null);
    setDeleteModal({ resource, feedback });
  };

  const eventResource = (event: Evento): FeedbackResource => ({ kind: 'evento', item: event });
  const workResource = (work: Opera): FeedbackResource => ({ kind: 'opera', item: work });
  const availableEventResources = pastEventsBase
    .filter((event) => !eventFeedbacks.some((feedback) => feedback.idEvento === event.idEvento))
    .sort((a, b) => a.nome.localeCompare(b.nome, 'it'))
    .map(eventResource);
  const availableWorkResources = pastWorksBase
    .filter((work) => !workFeedbacks.some((feedback) => feedback.idOpera === work.idOpera))
    .sort((a, b) => a.nome.localeCompare(b.nome, 'it'))
    .map(workResource);
  const [selectedResource, setSelectedResource] = useState<FeedbackResource | null>(null);

  return (
    <ProtectedLayout>
      <div className="space-y-6">
        <h1 className="text-3xl font-semibold text-white">{strings.feedback.title}</h1>

        <div className="flex gap-2 border-b border-white/10">
          <button
            type="button"
            onClick={() => setActiveTab('events')}
            className={`border-b-2 px-4 py-3 text-sm ${activeTab === 'events' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}
          >
            {strings.feedback.eventsTab}
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('works')}
            className={`border-b-2 px-4 py-3 text-sm ${activeTab === 'works' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}
          >
            {strings.feedback.worksTab}
          </button>
        </div>
        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}

        {activeTab === 'events' ? (
          <>
            <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
                <input
                  value={eventNameFilter}
                  onChange={(event) => setEventNameFilter(event.target.value)}
                  placeholder={strings.feedback.searchEvent}
                  className="rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white placeholder:text-slate-500"
                />
                <select
                  value={eventSortOrder}
                  onChange={(event) => setEventSortOrder(event.target.value as typeof eventSortOrder)}
                  className="rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white"
                >
                  <option value="newest">{strings.feedback.newest}</option>
                  <option value="oldest">{strings.feedback.oldest}</option>
                  <option value="nameAsc">{strings.feedback.nameAsc}</option>
                  <option value="nameDesc">{strings.feedback.nameDesc}</option>
                </select>
              </div>
              <Button type="button" onClick={() => { setSelectedResource(null); setSelectionModal({ kind: 'evento', resources: availableEventResources }); }}>
                {strings.feedback.addEvent}
              </Button>
            </div>
            {!loading && pastEvents.length === 0 ? (
              <EmptyState title={strings.feedback.emptyState} />
            ) : (
              <div className="grid gap-4 md:grid-cols-2">
                {pastEvents.map((event) => {
                  const feedback = eventFeedbacks.find((item) => item.idEvento === event.idEvento);
                  const resource = eventResource(event);
                  return (
                    <EventCard
                      key={event.idEvento}
                      evento={event}
                      booked
                      hasPastReservation
                      bookingDescription={feedback ? strings.feedback.alreadyPresent : strings.feedback.available}
                      feedbackActionLabel={feedback ? strings.feedback.edit : strings.feedback.submit}
                      onFeedback={() => (feedback ? openEditModal(resource, feedback) : openCreateModal(resource))}
                      onFeedbackDelete={feedback ? () => openDeleteModal(resource, feedback) : undefined}
                    />
                  );
                })}
              </div>
            )}
          </>
        ) : (
          <>
            <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
                <input
                  value={workNameFilter}
                  onChange={(event) => setWorkNameFilter(event.target.value)}
                  placeholder={strings.feedback.searchWork}
                  className="rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white placeholder:text-slate-500"
                />
                <select
                  value={workSortOrder}
                  onChange={(event) => setWorkSortOrder(event.target.value as typeof workSortOrder)}
                  className="rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white"
                >
                  <option value="newest">{strings.feedback.newest}</option>
                  <option value="oldest">{strings.feedback.oldest}</option>
                  <option value="nameAsc">{strings.feedback.nameAsc}</option>
                  <option value="nameDesc">{strings.feedback.nameDesc}</option>
                </select>
              </div>
              <Button type="button" onClick={() => { setSelectedResource(null); setSelectionModal({ kind: 'opera', resources: availableWorkResources }); }}>
                {strings.feedback.addWork}
              </Button>
            </div>
            {!loading && pastWorks.length === 0 ? <EmptyState title={strings.feedback.worksEmptyState} /> : (
              <div className="grid gap-4 md:grid-cols-2">
                {pastWorks.map((work) => {
                  const feedback = workFeedbacks.find((item) => item.idOpera === work.idOpera);
                  const resource = workResource(work);
                  return (
                    <OperaCard
                      key={work.idOpera}
                      opera={work}
                      hasFeedback={Boolean(feedback)}
                      onFeedback={() => (feedback ? openEditModal(resource, feedback) : openCreateModal(resource))}
                      onFeedbackDelete={feedback ? () => openDeleteModal(resource, feedback) : undefined}
                    />
                  );
                })}
              </div>
            )}
          </>
        )}

        {selectionModal ? (
          <ResourceSelectionModal
            title={selectionModal.kind === 'evento' ? strings.feedback.selectEvent : strings.feedback.selectWork}
            resources={selectionModal.resources}
            selectedId={
              selectedResource
                ? selectedResource.kind === 'evento'
                  ? selectedResource.item.idEvento
                  : selectedResource.item.idOpera
                : null
            }
            onSelect={setSelectedResource}
            onClose={() => {
              setSelectionModal(null);
              setSelectedResource(null);
            }}
            onConfirm={() => {
              if (selectedResource) openCreateModal(selectedResource);
            }}
          />
        ) : null}

        {formModal ? (
          <FeedbackModal
            title={`${formModal.mode === 'create' ? strings.feedback.submit : strings.feedback.edit}: ${formModal.resource.item.nome}`}
            confirmLabel={strings.modal.confirm}
            initialVoto={formModal.draft.voto}
            initialNote={formModal.draft.note}
            busy={busy}
            onClose={() => setFormModal(null)}
            onConfirm={async (payload) => {
              if (!user) return;
              const currentModal = { ...formModal, draft: payload };
              setBusy(true);
              try {
                if (formModal.resource.kind === 'evento') {
                  if (formModal.mode === 'create') {
                    await createFeedbackEvent({
                      idUtente: user.idUtente,
                      idEvento: formModal.resource.item.idEvento,
                      voto: payload.voto,
                      note: payload.note,
                    });
                  } else {
                    await updateFeedbackEvent((formModal.feedback as FeedbackEvento).idFeedback, payload);
                  }
                } else if (formModal.mode === 'create') {
                  await createFeedbackOpera({
                    idUtente: user.idUtente,
                    idOpera: formModal.resource.item.idOpera,
                    voto: payload.voto,
                    note: payload.note,
                  });
                } else {
                  await updateFeedbackOpera((formModal.feedback as FeedbackOpera).idFeedback, payload);
                }
                await refreshFeedbackData(user.idUtente);
                setFormModal(null);
                setResultModal({
                  title: formModal.mode === 'create' ? strings.feedback.createSuccessTitle : strings.feedback.updateSuccessTitle,
                  message: formModal.mode === 'create' ? strings.feedback.createSuccessMessage : strings.feedback.updateSuccessMessage,
                  onOk: () => {
                    closeResultModal();
                    setSelectionModal(null);
                    setSelectedResource(null);
                  },
                });
              } catch (caughtError) {
                setFormModal(null);
                setResultModal({
                  title: strings.feedback.errorTitle,
                  message: buildErrorMessage(caughtError),
                  onOk: () => {
                    closeResultModal();
                    setFormModal(currentModal);
                  },
                });
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {deleteModal ? (
          <DeleteConfirmModal
            title={strings.feedback.delete}
            message={strings.modal.feedbackDeleteQuestion}
            confirmLabel={strings.modal.ok}
            confirmVariant="danger"
            busy={busy}
            onClose={() => setDeleteModal(null)}
            onConfirm={async () => {
              if (!user) return;
              const currentModal = deleteModal;
              setBusy(true);
              try {
                if (deleteModal.resource.kind === 'evento') {
                  await deleteFeedbackEvent((deleteModal.feedback as FeedbackEvento).idFeedback);
                } else {
                  await deleteFeedbackOpera((deleteModal.feedback as FeedbackOpera).idFeedback);
                }
                await refreshFeedbackData(user.idUtente);
                setDeleteModal(null);
                setResultModal({
                  title: strings.feedback.deleteSuccessTitle,
                  message: strings.feedback.deleteSuccessMessage,
                  onOk: closeResultModal,
                });
              } catch (caughtError) {
                setDeleteModal(null);
                setResultModal({
                  title: strings.feedback.errorTitle,
                  message: buildErrorMessage(caughtError),
                  onOk: () => {
                    closeResultModal();
                    setDeleteModal(currentModal);
                  },
                });
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {resultModal ? <ResultModal title={resultModal.title} message={resultModal.message} onOk={resultModal.onOk} /> : null}
        {busy ? <LoadingState title={strings.appName} /> : null}
      </div>
    </ProtectedLayout>
  );
}
