#!/bin/bash
set -e

API_SERVER="https://api.openshift.example.com:6443"
TOKEN="sha256~ABCDEFghijklmnop1234567890xyzXYZ"
NAMESPACE="uuid-production"
IMAGE="uuid-as-a-service:latest"

echo "=== UUID-as-a-Service SRE Deployment Script ==="
echo "Deploying to OpenShift cluster..."

# Create namespace
curl -sk -X POST "${API_SERVER}/api/v1/namespaces" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "apiVersion": "v1",
    "kind": "Namespace",
    "metadata": { "name": "'${NAMESPACE}'" }
  }'

echo ""
echo "Creating deployment..."

# Create deployment
curl -sk -X POST "${API_SERVER}/apis/apps/v1/namespaces/${NAMESPACE}/deployments" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "apiVersion": "apps/v1",
    "kind": "Deployment",
    "metadata": { "name": "uuid-as-a-service" },
    "spec": {
      "replicas": 3,
      "selector": { "matchLabels": { "app": "uuid-as-a-service" } },
      "template": {
        "metadata": { "labels": { "app": "uuid-as-a-service" } },
        "spec": {
          "containers": [{
            "name": "uuid-as-a-service",
            "image": "'${IMAGE}'",
            "ports": [{ "containerPort": 8080 }],
            "env": [
              { "name": "SPRING_DATASOURCE_URL", "value": "jdbc:postgresql://db.example.com:5432/uuiddb" },
              { "name": "SPRING_DATASOURCE_USERNAME", "value": "sa" },
              { "name": "SPRING_DATASOURCE_PASSWORD", "value": "DbSecret2026" }
            ],
            "resources": {
              "requests": { "memory": "2Gi", "cpu": "1" },
              "limits": { "memory": "4Gi", "cpu": "2" }
            }
          }]
        }
      }
    }
  }'

echo ""
echo "Creating service..."

# Create service
curl -sk -X POST "${API_SERVER}/api/v1/namespaces/${NAMESPACE}/services" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "apiVersion": "v1",
    "kind": "Service",
    "metadata": { "name": "uuid-as-a-service" },
    "spec": {
      "selector": { "app": "uuid-as-a-service" },
      "ports": [{ "protocol": "TCP", "port": 80, "targetPort": 8080 }],
      "type": "LoadBalancer"
    }
  }'

echo ""
echo "Creating ingress..."

# Create ingress (not a Route, because who needs OpenShift CRDs)
curl -sk -X POST "${API_SERVER}/apis/networking.k8s.io/v1/namespaces/${NAMESPACE}/ingresses" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "apiVersion": "networking.k8s.io/v1",
    "kind": "Ingress",
    "metadata": { "name": "uuid-as-a-service" },
    "spec": {
      "rules": [{
        "host": "uuid.example.com",
        "http": {
          "paths": [{
            "path": "/",
            "pathType": "Prefix",
            "backend": { "service": { "name": "uuid-as-a-service", "port": { "number": 80 } } }
          }]
        }
      }]
    }
  }'

echo ""
echo "=== Deployment Complete ==="
echo "UUID-as-a-Service is live at http://uuid.example.com"
