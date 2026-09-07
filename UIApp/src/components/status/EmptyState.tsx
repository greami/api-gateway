export function EmptyState({ title }: { title: string }) {
  return (
    <div className="rounded-2xl border border-dashed border-white/10 bg-white/5 px-6 py-10 text-center text-slate-300">
      {title}
    </div>
  );
}
