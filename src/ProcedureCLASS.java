public class ProcedureCLASS {

  public int uint(int signint) {
   if (signint > -1)
    return signint;
   else
    return (signint + 256);
  }

  public int cyfra(int c_liczba, int c_miejsce) { //max 9999999 (9,9mln)
   int c_temp = 0;
   int zwrot = -1;
   int potega = 10;
   for (c_temp = 0; c_temp < c_miejsce; c_temp++)
    potega = (potega * 10);
   c_temp = (c_liczba / potega) * potega;
   zwrot = c_liczba - (c_temp);
   if (c_miejsce > 0)
    zwrot = zwrot / (potega / 10);
   return zwrot;
  }

  public int casttime(int numer_spella) {
   int zwrot = 3000;
   switch (numer_spella) {
    case 1:
     zwrot = 2500;
     break;
    case 102:
     zwrot = 3500;
     break; //heal
   }
   return zwrot;
  }

  public int exp(int level) {
   int zwrot = -1;
   int exp_temp = 15 * (level / 5);
   zwrot = 23 + exp_temp + (level * level * 86);
   return zwrot;
  }

  public int zasieg_spella(int nr_spella) {
   int wynik = 2;
   switch (nr_spella) {
    case 1:
     wynik = 6;
     break;
    case 102:
     wynik = 8;
     break;
   }
   return wynik;
  }

  public int przelicz_menu_char(int menx, int meny) {
   int wynik = 0;
   if (menx == 0) {
    wynik = meny;
   } else {
    wynik = 14 + meny;
   }
   if (wynik == 20) wynik = 10; //(prawy pierscien nr 1)
   return wynik;
  }

  public int odleglosc(int odleglosc_x, int odleglosc_y, int odleglosc_c) {
   int zwrot = 0;
   if (((odleglosc_x * odleglosc_x) + (odleglosc_x * odleglosc_x)) <= (odleglosc_c * odleglosc_c)) {
    zwrot = 1;
   }
   return zwrot;
  }


}
