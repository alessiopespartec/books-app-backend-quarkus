## Bug Fix
- [ ] Nelle POST di Book:
  - quando inserisco un Publisher mi va in eccezione l'API, ignorando la validazione se esiste o meno.
  - quando inserisco un Author con id che esiste, sembra che mi crei un nuovo autore (tutti i campi come null). Infatti se ottengo il libro, non mi appare associato ad alcun autore.