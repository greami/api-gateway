import { useEffect, useState } from 'react';
import { formatDateTime, isFutureDate } from '../../lib/date';
import type { Evento } from '../../lib/types';
import { Button } from '../ui/Button';
import { strings } from '../../config/strings';
import { getAvailableBookingsByEvent, getOpereByEvento } from '../../lib/museumApi';
import { Modal } from '../ui/Modal';
import { AvailabilityProgress } from './AvailabilityProgress';

export interface EventCardProps {
  evento: Evento;
  booked: boolean;
  hasPastReservation: boolean;
  onBook?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onFeedback?: () => void;
  feedbackActionLabel?: string;
  onFeedbackDelete?: () => void;
  bookingDescription?: string;
}

export function EventCard({
  evento,
  booked,
  hasPastReservation,
  onBook,
  onEdit,
  onDelete,
  onFeedback,
  feedbackActionLabel,
  onFeedbackDelete,
  bookingDescription,
}: EventCardProps) {
  const future = isFutureDate(evento.data);
  const [works, setWorks] = useState<Awaited<ReturnType<typeof getOpereByEvento>>>([]);
  const [worksOpen, setWorksOpen] = useState(false);
  const [worksLoading, setWorksLoading] = useState(false);
  const [worksError, setWorksError] = useState(false);
  const [availability, setAvailability] = useState<{ available: number; total: number } | null>(null);
  const [availabilityLoading, setAvailabilityLoading] = useState(true);

  useEffect(() => {
    let active = true;
    setAvailabilityLoading(true);
    void getAvailableBookingsByEvent(evento.idEvento)
      .then((response) => {
        if (active) {
          setAvailability({
            available: response.numeroPostiDispobili,
            total: response.numeroPostiTotali,
          });
        }
      })
      .catch(() => {
        if (active) setAvailability(null);
      })
      .finally(() => {
        if (active) setAvailabilityLoading(false);
      });

    return () => {
      active = false;
    };
  }, [evento.idEvento]);

  const openWorks = async () => {
    setWorksOpen(true);
    setWorksLoading(true);
    setWorksError(false);
    try {
      setWorks(await getOpereByEvento(evento.idEvento));
    } catch {
      setWorksError(true);
    } finally {
      setWorksLoading(false);
    }
  };

  return (
    <article className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg">
      <div className="flex flex-col gap-4">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0">
            <div className="text-xl font-semibold text-white">{evento.nome}</div>
            <div className="mt-1 text-sm text-slate-400">
              {formatDateTime(evento.data)} · {evento.durata}h
            </div>
            {bookingDescription ? <p className="mt-3 text-sm text-slate-300">{bookingDescription}</p> : null}
          </div>
          <AvailabilityProgress availability={availability} loading={availabilityLoading} />
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <span className="rounded-full bg-white/10 px-3 py-1 text-xs text-slate-200">
            {booked ? strings.bookings.bookedLabel : strings.bookings.notBookedLabel}
          </span>
          <span className="rounded-full bg-white/10 px-3 py-1 text-xs text-slate-200">
            {future ? strings.shared.futureLabel : strings.shared.pastLabel}
          </span>
        </div>

        <div className="flex flex-wrap gap-3">
          <Button variant="secondary" onClick={() => void openWorks()}>
            {strings.bookings.works}
          </Button>
          {future && !booked && onBook ? <Button onClick={onBook}>{strings.bookings.book}</Button> : null}
          {future && booked && onEdit ? <Button onClick={onEdit}>{strings.bookings.edit}</Button> : null}
          {future && booked && onDelete ? (
            <Button variant="danger" onClick={onDelete}>
              {strings.bookings.delete}
            </Button>
          ) : null}
          {hasPastReservation && !future && onFeedback ? (
            <Button onClick={onFeedback}>{feedbackActionLabel ?? strings.feedback.submit}</Button>
          ) : null}
          {hasPastReservation && !future && onFeedbackDelete ? (
            <Button variant="danger" onClick={onFeedbackDelete}>
              {strings.feedback.delete}
            </Button>
          ) : null}
        </div>
      </div>
      {worksOpen ? (
        <Modal
          title={`${strings.bookings.worksTitle}: ${evento.nome}`}
          onClose={() => setWorksOpen(false)}
          scrollable
          actions={
            <Button type="button" variant="secondary" onClick={() => setWorksOpen(false)}>
              {strings.bookings.close}
            </Button>
          }
        >
          {worksLoading ? <p className="text-sm text-slate-300">{strings.status.loading}</p> : null}
          {worksError ? <p className="text-sm text-rose-300">{strings.bookings.worksError}</p> : null}
          {!worksLoading && !worksError && works.length === 0 ? (
            <p className="text-sm text-slate-300">{strings.bookings.worksEmpty}</p>
          ) : null}
          {!worksLoading && !worksError ? (
            <ul className="space-y-3">
              {works.map((work) => (
                <li key={work.idOpera} className="rounded-lg bg-white/5 p-3">
                  <div className="font-medium text-white">{work.nome}</div>
                  <div className="text-sm text-slate-400">{work.autore}</div>
                </li>
              ))}
            </ul>
          ) : null}
        </Modal>
      ) : null}
    </article>
  );
}
