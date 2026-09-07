import { formatDateTime, isFutureDate } from '../../lib/date';
import type { Evento } from '../../lib/types';
import { Button } from '../ui/Button';
import { strings } from '../../config/strings';

export interface EventCardProps {
  evento: Evento;
  booked: boolean;
  hasPastReservation: boolean;
  onBook?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onFeedback?: () => void;
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
  bookingDescription,
}: EventCardProps) {
  const future = isFutureDate(evento.data);

  return (
    <article className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg">
      <div className="flex flex-col gap-4">
        <div>
          <div className="text-xl font-semibold text-white">{evento.nome}</div>
          <div className="mt-1 text-sm text-slate-400">
            {formatDateTime(evento.data)} · {evento.durata}h
          </div>
          {bookingDescription ? <p className="mt-3 text-sm text-slate-300">{bookingDescription}</p> : null}
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
          {!future && onFeedback ? <Button onClick={onFeedback}>{strings.feedback.submit}</Button> : null}
          {future && !booked && onBook ? <Button onClick={onBook}>{strings.bookings.book}</Button> : null}
          {future && booked && onEdit ? <Button onClick={onEdit}>{strings.bookings.edit}</Button> : null}
          {future && booked && onDelete ? (
            <Button variant="danger" onClick={onDelete}>
              {strings.bookings.delete}
            </Button>
          ) : null}
          {hasPastReservation && !future && onFeedback ? <Button onClick={onFeedback}>{strings.feedback.submit}</Button> : null}
        </div>
      </div>
    </article>
  );
}
