import { strings } from '../../config/strings';

export function AvailabilityProgress({
  availability,
  loading,
  size = 'large',
  showLabel = true,
}: {
  availability: { available: number; total: number } | null;
  loading: boolean;
  size?: 'small' | 'large';
  showLabel?: boolean;
}) {
  const radius = 27;
  const circumference = 2 * Math.PI * radius;
  const ratio = availability && availability.total > 0 ? Math.min(Math.max(availability.available / availability.total, 0), 1) : 0;
  const value = loading || !availability ? '--/--' : `${availability.available}/${availability.total}`;
  const progressColor = ratio === 1 ? '#767676' : `hsl(${120 * (1 - ratio)} 85% 45%)`;
  const dimensions = size === 'small' ? 'h-16 w-16' : 'h-20 w-20';

  return (
    <div className="flex shrink-0 flex-col items-center gap-1" aria-label={strings.bookings.availability}>
      <svg viewBox="0 0 64 64" className={`${dimensions} -rotate-90`} role="img">
        <circle cx="32" cy="32" r={radius} fill="none" stroke="currentColor" strokeWidth="5" className="availability-track text-white/10" />
        <circle
          cx="32"
          cy="32"
          r={radius}
          fill="none"
          stroke="currentColor"
          strokeWidth="5"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={circumference * (1 - ratio)}
          style={{ color: progressColor }}
          className="transition-all"
        />
        <text
          x="32"
          y="32"
          textAnchor="middle"
          dominantBaseline="central"
          transform="rotate(90 32 32)"
          className={`fill-current font-semibold ${size === 'small' ? 'text-[12px]' : 'text-[11px]'}`}
        >
          {value}
        </text>
      </svg>
      {showLabel ? <span className="text-xs text-slate-400">{strings.bookings.availability}</span> : null}
    </div>
  );
}
