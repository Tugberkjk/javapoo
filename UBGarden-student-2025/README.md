# UGarden 2025
#Auteurs
Hasan Tugberk Botanlioglu
Ege Ucer

Nous avons réalisé ce projet pour le cours de Programmation Orientée Objet. Le jeu de base, UBGarden, nous a été fourni au début. Notre objectif était de compléter et développer ce jeu en suivant les consignes données.Nous avons donc intégré des objets comme les guêpes, les frelons, les pommes ou encore les carottes, ainsi qu’un système de Level .Le joueur peut maintenant ramasser des objets, utiliser des bombes pour tuer les guêpes et les frelons ou s’il n’a pas des bombes il risque de se faire piquer par eux ,et ensuite changer de niveau.

Bonus:

Pour rendre tous les bonus visibles dans le jeu, nous avons décidé d’ajouter un sprite spécifique pour chaque type de bonus (par exemple SpriteApple, SpritePoisonedApple, SpriteCarrots, etc.) Chaque bonus possède ainsi une entrée correspondante dans SpriteFactory, ce qui permet un affichage automatique et cohérent sur la carte via la fabrique.

Nous avons structuré les bonus autour de la classe abstraite Bonus afin de factoriser la logique commune tout en permettant des comportements spécifiques par sous-classe. Par exemple, nous avons surchargé la méthode pickUpBy dans Apple pour restaurer l’énergie du jardinier et le guérir, tandis que PoisonedApple augmente le diseaseLevel et déclenche un effet temporaire. Carrots, de son côté, déclenche l’ouverture des portes après collecte complète. Grâce au polymorphisme, nous avons pu faire en sorte que chaque bonus définisse son propre effet sans modifier la logique générale du jeu, en rendant la collecte automatique dès que le jardinier entre sur la case.


Decor:

Pour les décors, nous avons également ajouté une entrée spécifique pour chaque type de décor dans SpriteFactory, ce qui permet à la fabrique de retourner automatiquement le bon SpriteDecor en fonction de l’instance de décor rencontrée.

Nous avons structuré les décors à partir de la classe abstraite Decor, qui représente tous les éléments statiques de la carte. Chaque sous-classe (par exemple Grass, Land, Tree, Flowers, DoorNextClosed) surcharge la méthode walkableBy pour définir si elle est franchissable ou non par le jardinier ou par d’autres entités. Cela nous a permis de spécifier clairement les règles de déplacement : par exemple, les insectes peuvent traverser les bosquets de fleurs mais pas les arbres ; les portes fermées bloquent tout le monde jusqu’à leur ouverture. Cette approche nous a permis d’éviter des vérifications instanceof dispersées dans le code et de gérer proprement les interactions entre personnages et décor. Nous avons aussi conçu les décors pour qu’ils puissent contenir des bonus, ce qui rend possible l’apparition dynamique d’objets comme les bombes générées par les nids.

Gardener:

La classe Gardener a été modifiée pour gérer l’énergie, la fatigue (diseaseLevel) et les bombes. Chaque déplacement consomme de l’énergie selon le terrain et le niveau de fatigue. Le jardinier récupère de l’énergie en restant immobile et guérit en ramassant une pomme. La méthode update() gère la récupération d’énergie et la diminution progressive du diseaseLevel après un délai (diseaseDuration). Lors du ramassage de bonus, pickUpBy() applique les effets spécifiques (ex. guérison, ajout de bombes, augmentation de fatigue).

Wasp et Hornet:

Nous avons implémenté les classes Wasp et Hornet avec une structure commune : déplacement aléatoire selon une fréquence (waspMoveFrequency, hornetMoveFrequency), génération par leurs nids respectifs (toutes les 5s pour les guêpes, 10s pour les frelons), et gestion des collisions dans checkCollision de GameEngine. Les guêpes meurent après un coup ; les frelons nécessitent deux coups (health=2). Chaque niveau conserve sa propre liste de wasps et hornets dans Level, évitant leur déplacement entre niveaux.

Pour l’affichage, nous avons ajouté les fichiers de sprites et créé les classes SpriteWasp et SpriteHornet dans view, intégrées dans GameEngine pour un rendu dynamique et directionnel des insectes sur la carte.


Win,Lost :

Les conditions de victoire et de défaite sont vérifiées dans update() de GameEngine. On gagne si le jardinier atteint le hérisson, et on perd si son énergie tombe sous 0. Dans les deux cas, on arrête la boucle et on affiche un message via showMessage().


Doors, Level changes :

Nous avons implémenté une logique de changement de niveau basée sur les types de portes présents sur la carte. On a ces trois types des portes DoorNextClosed, DoorNextOpened ( >)  et DoorPrevOpened (<). Les portes fermées empêchent le passage tant que toutes les carottes du niveau ne sont pas ramassées. Après la collecte des carottes, la porte est remplacée dynamiquement par une DoorNextOpened.
Lorsque le joueur se place sur une case contenant une DoorNextOpened ou une DoorPrevOpened, la méthode move() du jardinier déclenche un appel à game.requestSwitchLevel() avec le numéro du niveau suivant ou précédent selon la porte. Le moteur de jeu (GameEngine) détecte ensuite ce changement dans checkLevel(), met à jour le niveau courant du World, et place le jardinier à la position de la porte appropriée dans le nouveau niveau (grâce à une recherche dans les entités du décor).
