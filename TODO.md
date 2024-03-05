## Bug Fix

Quando faccio la PATCH del libro e non inserisco la voce "authors":
mi azzera gli autori di quel libro (e viceversa).
Deve invece ignorarmi i valori del payload e non modificare gli autori del libro.

Quando faccio una POST del libro,
non mi tornano i valori aggiornati degli autori e dei publisher.
Invece con le GET tornano aggiornati.

PATCH di Publisher restituisce un errore, ma va nel database.