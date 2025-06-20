# code-with-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

## Deploy image to docker hub

    docker build -f docker/Dockerfile.jvm -t lgomezs/quarkus-app:1.0 .
    docker login
    docker push lgomezs/quarkus-app:1.0

## Deploy in minikube

 ``` devops/
  ├── k8s/
  │   ├── postgress.yaml           # Genera pod de postgress y el service NOde port.
  │   ├── init-schema-configmap.yaml             # Crea el SCHEMA de postgress
  │
  ├── monitoring/
  │   ├── servicemonitor.yaml       # Para Prometheus detecte métricas de Quarkus
  │   ├── prometheus-rule.yaml      # Regla que alerta si la BD está caída
  │   ├── values.yaml               # Configuración SMTP para enviar correos
  │  
```

  ```
    minikube start --driver=virtualbox
    minikube create namespace applications
    minikube create namespace monitoring

    kubectl apply -f /devops/k8s/*..yaml
  ```

  ### Install prometheus, grafana y configure alerts:

   ```
      helm upgrade --install prom-stack prometheus-community/kube-prometheus-stack \
      --namespace monitoring \
      -f values.yaml \
      --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false \
      --set prometheus.prometheusSpec.serviceMonitorNamespaceSelector.matchNames[0]=applications
   ```

   For notification you must have a Gmail password app, generate from: https://myaccount.google.com/apppasswords, then generate secret :
   
  ```
      kubectl create secret generic alertmanager-gmail-secret \
      --namespace monitoring \
      --from-literal=smtp_pass='HERE-PASSWOD-GENERATE-FROM-GMAIL'

      kubectl apply -f /devops/monitoring/*..yaml
  ```
     
  #### Validate application: GET: http://localhost:8080/customer

  #### Validate Healths:

    kubectl port-forward svc/myapp 8080:8080 -n applications

  Health check:     http://localhost:8080/q/health/ready

  Metric prometheus:     http://localhost:8080/q/metrics

 ## Port-forward for validate prometheus, grafana and alert manager:

  #### Prometheus:   
    kubectl port-forward -n monitoring prometheus-prom-stack-kube-prometheus-prometheus-0 9090:9090 

  http://localhost:9090/query

   ![Screenshot from running application](images/prometheus.png?raw=true "Screenshot")

   ![Screenshot from running application](images/health-quarkus.png?raw=true "Screenshot")

   ![Screenshot from running application](images/metrics-ds-firing.png?raw=true "Screenshot")

  #### Grafana: 
    kubectl port-forward -n monitoring prom-stack-grafana-594f699b5-l5n2g 3000:3000
  
  http://localhost:3000/login
    admin/prom-operator

  #### Alert manager: 
    kubectl port-forward -n monitoring alertmanager-prom-stack-kube-prometheus-alertmanager-0 9093

  http://localhost:9093

  Ver conf de alert manager:
  
    kubectl get secret -n monitoring alertmanager-prom-stack-kube-prometheus-alertmanager \
    -o jsonpath='{.data.alertmanager\.yaml}' | base64 -d

   ![Screenshot from running application](images/alertmanager.png?raw=true "Screenshot")

### View notifications in our email.

  ![Screenshot from running application](images/notification-email.png?raw=true "Screenshot")

## Export metrics to Grafana and display dashboards with statuses

 ![Screenshot from running application](images/dashboard-panel-firing.png?raw=true "Screenshot")

 ![Screenshot from running application](images/micrometer-metrics.png?raw=true "Screenshot")

## Related Guides

- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Reactive PostgreSQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the PostgreSQL database using the reactive pattern
- Blaze-Persistence ([guide](https://quarkus.io/guides/blaze-persistence)): Advanced SQL support for JPA and Entity-Views as efficient DTOs