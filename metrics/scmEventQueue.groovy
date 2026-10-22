import java.util.concurrent.Delayed
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

def SAMPLE_SECONDS = 10
def m = jenkins.scm.api.SCMEvent.getDeclaredMethod('executorService')
m.accessible = true

def findPool
findPool = { obj, depth ->
  if (obj instanceof ThreadPoolExecutor) return obj
  if (obj == null || depth > 4) return null
  for (def c = obj.getClass(); c != null; c = c.superclass) {
    for (f in c.declaredFields) {
      if (java.lang.reflect.Modifier.isStatic(f.modifiers) || f.type.isPrimitive()) continue
        try { f.accessible = true } catch (ignored) { continue }
      def p = findPool(f.get(obj), depth + 1)
      if (p) return p
    }
  }
}

def pool = findPool(m.invoke(null), 0)
if (!pool) { println 'could not unwrap SCMEvent executor'; return }

def snapshot = {
  // Queue entries are delayed tasks: delay > 0 = still in the ~5s event delay,
  // delay <= 0 = ready and waiting for a free thread (how far below 0 = how long it's waited).
  long[] delays = pool.queue.toArray().findAll { it instanceof Delayed }
  .collect { it.getDelay(TimeUnit.MILLISECONDS) } as long[]
  
  def ready = delays.findAll { it <= 0 }
  [time: new Date(), core: pool.corePoolSize, active: pool.activeCount,
   queued: delays.length, ready: ready.size(), delayed: delays.length - ready.size(),
   oldestWaitSec: ready ? (long) (-ready.min() / 1000L) : 0L,
   completed: pool.completedTaskCount]
}

def a = snapshot()
Thread.sleep(SAMPLE_SECONDS * 1000L)

def b = snapshot()

[a, b].each { s ->
  println "${s.time}  core=${s.core} active=${s.active} queued=${s.queued} " +
    "(ready=${s.ready} delayed=${s.delayed}) oldestWait=${s.oldestWaitSec}s completed=${s.completed}"
}

long done = b.completed - a.completed
long netQueue = b.queued - a.queued
double rate = done / (double) SAMPLE_SECONDS
double inflow = (netQueue + done) / (double) SAMPLE_SECONDS

println String.format('throughput=%.2f events/s  inflow=%.2f events/s  net queue change=%+d in %ds', rate, inflow, netQueue, SAMPLE_SECONDS)

println(rate > 0 ? String.format('est. drain of ready backlog at current rate (ignoring new events)=%d min', (long) (b.ready / rate / 60.0d)) : 'est. drain=n/a (no events completed in sample)')

println(inflow > rate ? String.format('falling behind: inflow is %.1fx throughput', rate > 0 ? inflow / rate : Double.POSITIVE_INFINITY) : 'keeping up: throughput >= inflow')

println(netQueue)
return netQueue
null
