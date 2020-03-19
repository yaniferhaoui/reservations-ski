
# MPD (Modèle Physique des Données)
<h1 align="center">
    <img src="https://i.ibb.co/f2SDTwD/MLD.jpg">
</h1>

# Input
Personnes
* Indirectement: Via l'importation de booking.json
* Directement: Via l'importation de persons.json
* Directement: Via l'interface graphique du programme

Equipements
* Indirectement: Via l'importation de booking.json
* Directement: Via l'importation de equipments.json
* Directement: Via l'interface graphique du programme

Booking
* Directement: Via l'importation de booking.json
* Directement: Via l'interface graphique du programme

# Output
Chaque liste d'objets (Personnes, Equipements, Booking) peut être exportée dans un fichier Json

# Contraintes/Fonctionnalitées
* Un casque est automatiquement ajouté à un Booking lors de la réservation d'un équipement pour une personne de moins de 10ans. Seulement si l'équipement en question n'est pas un casque et qu'un casque n'est pas déjà réservé dans le Booking au nom de cette même personne.
* Des bâtons sont automatiquement ajoutés à un Booking lors de la réservation d'un équipement Ski. Seulement si des bâtons ne sont pas déjà dans le Booking au nom de cette même personne.
* Lors d'une réservation, la date de départ doit être inférieur à la date de fin
* Lors de l'enregistrement d'une personne, sa date de naissance doit être inférieur à la date courante
* Plus de contraintes/fonctionnalités à découvrir dans le programme

# Screenshots

<h1 align="center">
    <img src="https://i.ibb.co/TWMGyrP/1.png" alt="1" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/7KvJf3W/2.png" alt="2" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/JCphZtZ/3.png" alt="3" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/860RCWp/4.png" alt="4" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/mN1WT3L/5.png" alt="5" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/6gfw4pD/6.png" alt="6" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/Dpm165j/7.png" alt="7" border="0">
</h1>
<h1 align="center">
    <img src="https://i.ibb.co/mc77KXP/8.png" alt="8" border="0">
</h1>
