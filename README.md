# LDN10
## Programiranje vticev
---
Domaca naloga za RK, LDN7 je potrebno dodati SSL podporo.

## Uporaba
 - vse skupaj se compilea z `./compileScript.sh`
 - če `json-simple-1.1.1.jar` ni v PATH, je treba v vsakem shellu v katerem se ChatClient ali ChatServer odpre zagnati še `. envExport.sh`  
 - server se prižge z `java -cp json-simple-1.1.1.jar:. ChatServer`
 - client(i) se prižgejo z `java -cp json-simple-1.1.1.jar:. ChatClient`
 - ko je client prižgan pričakuje vpisano eno-besedno uporabniško ime.
 - client potem posluša na stdin. 
 - karkoli je napisano v stdin in se konča z newline je poslano kot `[PUBLIC]`, torej vsem v chatu.
 - katerikoli message se začne z `@[uporabnisko-ime]` bo tretiran kot zasebno sporočilo in bo poslan izključno clientu z `uporabnisko-ime` (v primeru da obstaja) če ne obstaja, bo poslan `ERROR` message.
 - vse za `@[uporabnisko-ime]` bo tretirano kot sporočilo (vključno s prvim presledkom).

(Dana navodila spisana za Ubuntu Linux 18.04, kjer je bil program tudi razvit)
## Zapiski
 - java projects VSCode extension je uporaben da VSCode ne javi json-simple importa kot error.  
 - da reč dela je treba zagnati `. envExport.sh`
 - compilea se z `./compileScript.sh`

## Viri
https://rkvaje.lrk.si/T07/T07.html
https://rkvaje.lrk.si/T10/T10.html
https://ucilnica.fri.uni-lj.si/mod/assign/view.php?id=15186