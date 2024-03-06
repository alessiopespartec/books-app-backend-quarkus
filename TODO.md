## Bug Fix

- [ ] Risolvere comportamento richiesta **PATCH** per un libro in modo che,
se l'attributo "authors" non è presente nel payload,
gli autori attualmente associati al libro rimangano inalterati.
Attualmente, l'assenza dell'attributo "authors" nel
payload porta alla rimozione di tutti gli autori associati al libro.

- [x] Problema nella richiesta POST di creazione di un libro, 
per cui non vengono restituiti i dati aggiornati degli autori e dell'editore.
Diversamente, utilizzando la richiesta GET, i dati vengono mostrati
correttamente aggiornati.

3. Correggere l'errore nella gestione della richiesta **PATCH**
per gli editori, che, nonostante restituisca un messaggio di errore,
effettua comunque le modifiche nel database.