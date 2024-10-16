Užduotis: Loterijos bilietų generavimas ir tikrinimas naudojant Redis

Užduoties aprašymas:

Šioje užduotyje reikia sukurti „Java“ programą, kuri generuoja loterijos bilietus, saugo juos „Redis“ ir tikrina juos pagal atsitiktinai sugeneruotus laimingus skaičius.

Funkcionalumas:
Programa turi turėti meniu su dviem pasirinkimais:

Generuoti tiražą: Sugeneruoja nurodytą bilietų skaičių, kiekvieną iš jų priskiriant unikalų ID (UUID), bilietai susideda iš 5 atsitiktinių skaičių nuo 1 iki 35. Kiekvienas bilietas išsaugomas Redis su UUID kaip raktu ir loterijos skaičiais kaip reikšme.

Žaisti: Sugeneruoja laimingus skaičius (5 skaičius nuo 1 iki 35), patikrina visus saugomus bilietus iš Redis ir apskaičiuoja laimėjimą pagal atitiktį tarp bilieto skaičių ir laimingų skaičių:

1 teisingas skaičius: 0,50 EUR.
2 teisingi skaičiai: 3 EUR.
3 teisingi skaičiai: 15 EUR.
4 teisingi skaičiai: 500 EUR.
5 teisingi skaičiai: 5000 EUR.

Detalės:
Programa turi prašyti vartotojo įvesti bilietų kiekį generuojant tiražą.

Po kiekvieno bilieto sugeneravimo, bilietas turi būti saugomas Redis su UUID ir skaičių sąrašu kaip vertė.

Žaidimo metu sugeneruojami 5 laimingi skaičiai ir visi išsaugoti bilietai tikrinami prieš šiuos skaičius, atspausdinant kiekvieno bilieto atitikimą ir laimėtą sumą.

Užduoties reikalavimai:
Naudok Redis kaip duomenų saugyklą bilietams saugoti.
Naudok UUID kiekvienam bilietui identifikuoti.
Programa turi palaikyti du pasirinkimus – generuoti bilietus ir žaisti.
Bilietų ir laimingų skaičių generavimas turi būti atsitiktinis, o laimėjimas apskaičiuojamas remiantis teisingų skaičių atitikimu.

Užduoties pavyzdys:

Generuoti tiražą:

Vartotojas įveda: 3 bilietai.

Sugeneruojami ir saugomi trys bilietai Redis:

UUID: a1b2c3d4-e5f6-7g8h-9i0j Skaičiai: [5, 10, 22, 30, 33]

UUID: b2c3d4e5-f6g7-8h9i-0j1k Skaičiai: [1, 6, 19, 24, 32]

UUID: c3d4e5f6-g7h8-9i0j-1k2l Skaičiai: [8, 14, 23, 28, 35]

Žaisti:

Sugeneruojami laimingi skaičiai: [5, 10, 19, 24, 30].

Programa patikrina visus bilietus ir išveda rezultatą:

Bilietas UUID: a1b2c3d4-e5f6-7g8h-9i0j atitiko 4 skaičius. Laimėta suma: 500 EUR.

Bilietas UUID: b2c3d4e5-f6g7-8h9i-0j1k atitiko 3 skaičius. Laimėta suma: 15 EUR.

Bilietas UUID: c3d4e5f6-g7h8-9i0j-1k2l atitiko 0 skaičius. Laimėjimo nėra.

Užduoties struktūra:

Pagrindinė programa su meniu: Programa turi paprašyti vartotojo pasirinkti tarp dviejų veiksmų: „Generuoti tiražą“ arba „Žaisti“.
Bilietų generavimas: Vartotojas įveda bilietų kiekį, sugeneruojami atsitiktiniai bilietai ir jie saugomi Redis.
Žaidimas: Sugeneruojami laimingi skaičiai ir kiekvienas bilietas tikrinamas prieš šiuos skaičius, atspausdinamas laimėjimo rezultatas.

Techninės detalės:

Naudoti „Redis“ ir „Jedis“ biblioteką „Java“ programoje bilietams saugoti ir gauti.

Naudoti UUID generuoti unikaliems bilietų ID.

Bilietai turėtų būti saugomi „Redis“ JSON formatu arba kaip sąrašas.
