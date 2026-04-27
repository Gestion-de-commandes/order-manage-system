# Script de test des dates de commits
# Vérifie que les dates sont correctement appliquées

Write-Host "=== Vérification des dates de commits ===" -ForegroundColor Cyan

# Vérifier si le repo git existe
if (!(Test-Path ".git")) {
    Write-Host "❌ Aucun repo git trouvé. Exécutez d'abord le script de commits." -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Historique des commits avec dates ===" -ForegroundColor Yellow
git log --oneline --format="%h - %s (%ad)" --date=short | Select-Object -First 10

Write-Host "`n=== Détails des 3 premiers commits ===" -ForegroundColor Yellow
$commits = git log --oneline -3 | ForEach-Object { $_.Split(' ')[0] }
foreach ($commit in $commits) {
    Write-Host "`nCommit: $commit" -ForegroundColor Green
    git show --format=fuller $commit | Select-String -Pattern "(AuthorDate|CommitDate)" | ForEach-Object {
        Write-Host "  $_" -ForegroundColor White
    }
}

Write-Host "`n=== Vérification des dates espacées ===" -ForegroundColor Yellow
$dates = git log --format="%ad" --date=iso | Select-Object -First 5
$uniqueDates = $dates | Sort-Object -Unique
Write-Host "Nombre total de dates différentes: $($uniqueDates.Count)" -ForegroundColor Cyan

if ($uniqueDates.Count -ge 5) {
    Write-Host "✅ Les dates sont correctement espacées !" -ForegroundColor Green
} else {
    Write-Host "❌ Problème: Les dates ne sont pas assez variées" -ForegroundColor Red
}

Write-Host "`n=== Test terminé ===" -ForegroundColor Cyan