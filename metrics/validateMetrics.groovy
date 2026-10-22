// Validate the custom metric was created

import jenkins.metrics.api.Metrics

def registry = Metrics.metricRegistry()
println registry.names.findAll { it.contains('scm') }
println registry.gauges['custom.scm.event.queue.depth']?.value
