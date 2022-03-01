# Problem do rozwiązania
Zakład usługowy

# Założenia
Pewien zakład usługowy zatrudnia łącznie 4 pracowników. Jedna osoba  
przyjmuje zamówienia na naprawę sprzętu X a trzy pozostałe osoby realizują te  
naprawy. Zakład może maksymalnie pomieścić 10 elementów sprzętu X. Osoba  
przyjmująca zamówienia notuje adresy właścicieli oddawanego przez nich  
sprzętu do naprawy i dołącza je do przyjmowanego egzemplarza. Potem  
odkłada sprzęt na półkę. Gdy któryś z trzech "fizycznych" naprawiaczy jest  
wolny to bierze odebrany element, naprawia go i wysyła do zamawiającego  
naprawę. Wysyłka z powrotem do klienta obsługiwana jest przez kuriera.

# Opis problemu
Klienci posiadają pewną ograniczoną pulę urządzeń. Każdy z nich zużywa swoje  
urządzenia po kolei a w momencie całkowitego zużycia przekazuje je do  
naprawy. Klient oczekuje aż do momentu przyjęcia zlecenia przez pracownika i  
dopiero w tedy zaczyna korzystać z kolejnego. Pracownik przyjmujący  
jednocześnie może zajmować się tylko jednym urządzeniem. Nadaje mu adres i  
oczekuje na wolne miejsce na półce. Jeżeli takie się pojawi odstawia na nią  
dane urządzenie i dopiero w tedy przyjmuje kolejne. Pracownik naprawiający  
oczekuje na pojawienie się urządzenia do naprawy na półce. Jeżeli takowe  
się pojawi zdejmuje je i dokonuje naprawy. Po naprawie oczekuje na dostępność  
kuriera który jest mu przypisany. Po przekazaniu kurierowi naprawionego  
urządzenia zdejmuje z półki kolejne. Kurier oczekuje na przyjęcie wysyłki i  
dostarcza ja do klienta o danym adresie. Po zużyciu wszystkich urządzeń i  
przekazaniu ich do naprawy klient oczekuje na otrzymanie każdego urządzenia  
z powrotem.

