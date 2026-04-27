$root = "C:\Users\hp\spring-boot-edge-microservices"

# Start PostgreSQL with Docker Compose
Write-Host "Starting PostgreSQL with Docker Compose..."
docker compose up -d
Start-Sleep -Seconds 10  # Wait for PostgreSQL to be ready

$services = @(
    @{ name="eureka-server"; path="$root\eureka-server" },
    @{ name="config-server"; path="$root\config-server" },
    @{ name="gateway-server"; path="$root\gateway-server" },
    @{ name="microservice-produits"; path="$root\microservice-produits" },
    @{ name="microservice-commandes"; path="$root\microservice-commandes" },
    @{ name="microservice-paiement"; path="$root\microservice-paiement" },
    @{ name="microservice-clientui"; path="$root\microservice-clientui" }
)

foreach ($svc in $services) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$($svc.path)'; .\mvnw.cmd spring-boot:run" -WindowStyle Normal
    Start-Sleep -Seconds 5  # Wait a bit between services to ensure proper startup order
}