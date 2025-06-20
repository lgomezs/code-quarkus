package org.acme.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class DynamicDatasourceHealthMetrics {

	@Inject
	MeterRegistry registry;

	private final Map<String, AtomicInteger> datasourceStatus = new ConcurrentHashMap<>();

	public void updateStatus(String name, boolean up) {
		this.datasourceStatus.computeIfAbsent(name, dsName -> {
			final AtomicInteger gauge = new AtomicInteger(up ? 1 : 0);
			this.registry.gauge("custom_health_datasource_status", Tags.of("datasource", dsName), gauge);
			return gauge;
		}).set(up ? 1 : 0);
	}

}
