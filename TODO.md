## Bug Fix
- [x] La POST di /authors accetta numeri in firstName o lastName e li trasforma in stringhe, deve accettare solo stringhe. Lo stesso per la PATCH, accetta anche numeri per firstName e lastName;
- [x] La PATCH di /authors restituisce 400 Bad Request se si inserisce un libro che non esiste. Deve restituire che il libro con id non trovato.
- [x] La **DELETE** di */authors* restituisce sempre 204 No Content, anche se l'autore corrispondente all'ID non esiste. Deve tornare un author not found se non lo trova. 
- [ ] Tutte le **CRUD** quando si inserisce un path parameter come testo (e non numeri) torna un 404 Not Found. Lo stesso se viene inserito null, 0 o un negative number. Controllare il response con un errore di "invalid type".

## Features