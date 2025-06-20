package org.acme.config;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Readiness
@ApplicationScoped
public class DynamicDatasourceHealthCheck implements HealthCheck {
	@Inject
	Instance<AgroalDataSource> datasources;

	@Inject
	DynamicDatasourceHealthMetrics metrics;

	@Override
	public HealthCheckResponse call() {
		final List<HealthCheckResponse> responses = new ArrayList<>();
		boolean allUp = true;

		for (final AgroalDataSource ds : this.datasources) {
			final String name = this.getDatasourceName(ds);
			final boolean up = this.isUp(ds);
			this.metrics.updateStatus(name, up);
			responses.add(HealthCheckResponse.named(name).status(up).build());
			if (!up) {
				allUp = false;
			}
		}

		return HealthCheckResponse.named("All datasources").status(allUp).build();
	}

	private boolean isUp(DataSource ds) {
		try (final Connection conn = ds.getConnection()) {
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	private String getDatasourceName(AgroalDataSource ds) {
		String name = ds.toString(); // fallback
		if (ds.getConfiguration() != null && ds.getConfiguration().connectionPoolConfiguration() != null) {
			final var cf = ds.getConfiguration().connectionPoolConfiguration();
			name = cf.connectionFactoryConfiguration().jdbcUrl();
		}
		return name;
	}

}
