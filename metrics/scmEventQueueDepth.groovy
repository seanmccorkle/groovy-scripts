// Monitor the backlog of incoming Source Control Management (SCM) webhook events (e.g. GitHub/GitLab push events) waiting to be processed by Jenkins

import jenkins.metrics.api.Metrics
import com.codahale.metrics.Gauge

def scmEventQueue() {
    // add logic here
    return 0
}

def registry = Metrics.metricRegistry()
registry.remove('custom.scm.event.queue.depth')   // make re-runs idempotent
registry.register('custom.scm.event.queue.depth', { scmEventQueue() } as Gauge) 
