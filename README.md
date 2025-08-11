
# code-with-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

Monitoring system that detects outages in a Quarkus application's data source and automatically notifies the technical team via email.

![Screenshot from running application](images/architecture.png?raw=true "Screenshot")

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

# Run application in local

 Run docker postgress.

    docker-compose down -v
    docker-compose up -d

    mvn clean install -DskipTests

 Metrics-> http://localhost:8080/q/metrics

 Validate application: GET: http://localhost:8080/customer

## Deploy image to docker hub

    docker build -f src/main/docker/Dockerfile.jvm -t lgomezs/quarkus-app:1.19 .
    docker login
    docker push lgomezs/quarkus-app:1.19

## Install minikube grafana y prometheus 

```
    Install minikube and kubectl:
        curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
        sudo install minikube-linux-amd64 /usr/local/bin/minikube
        
    minikube start
    kubectl create namespace applications
    kubectl create namespace monitoring
```
       helm upgrade --install prom-stack prometheus-community/kube-prometheus-stack \
        --namespace monitoring \
        -f values.yaml \
        --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false \
        --set prometheus.prometheusSpec.serviceMonitorNamespaceSelector.matchNames[0]=applications

Run prometheus en http://localhost:9090/
-> kubectl port-forward -n monitoring prometheus-prom-stack-kube-prometheus-prometheus-0 9090:9090

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

Get  IP_DATASOURCE, ip datasource, change it value in env microservice.yaml.

    #kubectl apply -f /devops/k8s/*..yaml
    kubectl apply -f /devops/k8s/ --recursive

Validate:  

http://localhost:8080/q/health/ready
http://localhost:8080/q/metrics

swaguer: http://localhost:8080/q/swaggerui/

#### Validate Healths from deploy minikube

    kubectl port-forward svc/myapp 8080:8080 -n applications

Health check:     http://localhost:8080/q/health/ready

Metric prometheus:     http://localhost:8080/q/metrics

### Configure alerts

   For notification you must have a Gmail password app, generate from: https://myaccount.google.com/apppasswords, then generate secret :
   
  ```
      kubectl create secret generic alertmanager-gmail-secret \
      --namespace monitoring \
      --from-literal=smtp_pass='password-aqui'

      kubectl apply -f /devops/monitoring/*..yaml
  ```     

 ## Port-forward for validate prometheus, grafana and alert manager:

   ![Screenshot from running application](images/prometheus.png?raw=true "Screenshot")

   ![Screenshot from running application](images/health-quarkus.png?raw=true "Screenshot")

   ![Screenshot from running application](images/metrics-ds-firing.png?raw=true "Screenshot")

  #### Grafana: 
    
    kubectl port-forward -n monitoring prom-stack-grafana-XXXXXX 3000:3000
  
  http://localhost:3000/login
    admin/prom-operator

  #### Alert manager: 
    kubectl port-forward -n monitoring alertmanager-prom-stack-kube-prometheus-alertmanager-0 9093

  http://localhost:9093

  Ver conf de alert manager:
  
    kubectl get secret -n monitoring alertmanager-prom-stack-kube-prometheus-alertmanager \
    -o jsonpath='{.data.alertmanager\.yaml}' | base64 -d

   ![Screenshot from running application](images/alertmanager.png?raw=true "Screenshot")

##  Install Loki con Helm

    helm repo add grafana https://grafana.github.io/helm-charts
    helm repo update
    helm install loki grafana/loki \
        --namespace monitoring \
        -f values.yaml

    helm install promtail grafana/promtail \
        --namespace monitoring \
        -f promtail-values.yaml

     helm upgrade promtail grafana/promtail \
        --namespace monitoring \
        -f promtail-values.yaml

-- validate promntail

     kubectl get pods -n monitoring -l app.kubernetes.io/name=promtail
     kubectl logs -n monitoring <pod-promtail>

## Install tempo 

    helm install tempo grafana/tempo \
    --namespace monitoring

    kubectl get svc -n monitoring

## Connect Grafana to Loki

  Uri data source loki: http://loki.monitoring.svc.cluster.local:3100

  IN Grafana: Configuration → Data Sources → Loki

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