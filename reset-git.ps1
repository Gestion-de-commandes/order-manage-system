# Script de nettoyage et test des commits
# Utilisez ce script pour recommencer proprement

Write-Host "=== Nettoyage de l'historique Git ===" -ForegroundColor Yellow

# Supprimer le repo git existant
if (Test-Path ".git") {
    Remove-Item -Recurse -Force .git
    Write-Host "✓ Dossier .git supprimé" -ForegroundColor Green
}

# Réinitialiser les variables d'environnement
$env:GIT_COMMITTER_DATE = $null
$env:GIT_AUTHOR_DATE = $null

Write-Host "✓ Variables d'environnement nettoyées" -ForegroundColor Green

# Réinitialiser la configuration git
git config --unset user.email
git config --unset user.name

Write-Host "✓ Configuration Git nettoyée" -ForegroundColor Green

Write-Host "`n=== Prêt pour un nouveau départ ===" -ForegroundColor Green
Write-Host "Vous pouvez maintenant exécuter : .\git-commits-script.ps1" -ForegroundColor Cyan