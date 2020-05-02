import java.io.*;
import javax.microedition.lcdui.game.*;

public class CommCLASS {

  //LegendsMIDlet legendsmidlet;
  LegendsCanvas legendscanvas;
  ProcedureCLASS procedureclass = new ProcedureCLASS();

  int spell_serw_odebrano = 0;
  int postac_klient = -1;
  int inventory_pobierz = -1; //-1
  int equip_pobierz = -1; //-1
  int inv_wyslalem = 0;
  int bajtow = 0;
  int bajtow_sent = 0;

  char[] char_array = new char[10];
  int[] buf = new int[250]; //bufor odbioru danych
  int[] buf2 = new int[20]; //bufor wysylki danych

  int i, j, k, l, m, n, o;
  String napis;

  public void Setlegendscanvas(LegendsCanvas lomidlet) {
      legendscanvas = lomidlet;
  }

  public void comm() {

   int wyj = 1;
   int sczytal = 1; //zmienna ktora sprawdza czy dane zostaly szczytane przy ostatniej petli - przy pierwszym przejsciu 1 zeby chocicaz raz przeszlo
   spell_serw_odebrano = 0; //zerowanie statusu o tym czy odebrano info o spellu
   while (sczytal == 1) { //jesli sczytane to mzoe cos jeszcze jest czytaj dalej, jesli nie wyjdz z petli
    sczytal = 0; //zerowanie zmiennej
    buf[0] = 0;
    try {
     StringBuffer sb = new StringBuffer();
     int c = 0;
     if (legendscanvas.is.available() != 0) { //jesli jest cos w buforze do sczytania
      sczytal = 1; //jesli cos jest to niech po tej petli jeszcze raz spobuje
      m = 0; //zmienna liczaca ktory bajt z rzedu czyta gra
      while (((c = legendscanvas.is.read()) != '\n') && (c != -1)) {
       sb.append((char) c);
       if (m < 250)
        buf[m] = procedureclass.uint((int) c); //zapisz dany znak do bufora
       m++;
      }
      napis = "" + sb.toString(); //nie uzywane juz ale niech zostanie
     }

    } catch (IOException ioe) {}

    if ((buf[0] == 68) && (m > 5)) { // jesli pierwszy znak to D - informacja o dmg
     postac_klient = -1; //jesli nie wiadomo czyje to dmg to zaczy ze to gracza
     i = ((buf[1] - 128) * 128) + buf[2] - 128; //numer postaci na serwie
     j = ((buf[3] - 128) * 128) + buf[4] - 128; //otrzymane dmg
     if (buf[5] == 127) //jesli to heal a nie dmg
      k = -1;
     else
      k = ((buf[5] - 128) * 128) + buf[6] - 128; //bonus expa
     for (n = 0; n < legendscanvas.ilosc_postaci; n++) { //szukanie czy postac jest zaladowana
      if (legendscanvas.postac_serw[n] == i) {
       postac_klient = n; //jesli tak to zapamietaj numer pod jakim jest przetrzymywana na kliencie
       break;
      }
     }
     if (k > 0) { //jesli gracz dostal expa
      legendscanvas.exp_wyswietl = 20;
      legendscanvas.exp_wartosc = k;
     }
     if (postac_klient > -1) {
      legendscanvas.dmg_arr[postac_klient] = 15;
      String bufor_dmg;
      String odstep = "   ";
      odstep = "";
      if (k == -1) {
       legendscanvas.dmg[postac_klient] = new TiledLayer(6, 1, legendscanvas.fontlblue, 4, 6);
      } else {
       legendscanvas.dmg[postac_klient] = new TiledLayer(6, 1, legendscanvas.fontbw, 4, 6);
      }
      if (j < 1000) odstep = " ";
      if (j < 10) odstep = "  ";
      bufor_dmg = "" + odstep + j;
      char[] bufor_dmg_c = bufor_dmg.toCharArray();
      for (n = 0; n < 6; n++) {
       if (bufor_dmg.length() > n)
        legendscanvas.dmg[postac_klient].setCell(n, 0, bufor_dmg_c[n] + 1);
       else
        legendscanvas.dmg[postac_klient].setCell(n, 0, 32 + 1);
      }
     }
    }

    if ((buf[0] == 65) && (m > 1)) { // A - informacja o castowanych spellach
     i = (buf[1] - 128);
     if (spell_serw_odebrano == 1)
      legendscanvas.spell_serw_info = i;
     else {
      legendscanvas.spell_serw_info_bufor[legendscanvas.spell_serw_info_pozycja] = i;
      legendscanvas.spell_serw_info_pozycja++;
     }
     spell_serw_odebrano = 1;
    }
    if ((buf[0] == 80) && (m > 1)) { // jesli pierwszy znak to P (postac) to transmisja dotyczy postaci
     if ((buf[3] == 82) || (buf[3] == 81)) { // jesli 4 znak trasmisji to Q (postac z nickiem) lub P (postac bez informacji o nicku)
      postac_klient = -1; //zmienna ktora bedzie ifnormacja pod jakim indeksem jest/bedzie ta postac na kliencie
      i = ((buf[1] - 128) * 128) + buf[2] - 128; //numer postaci na serwie
      j = ((buf[4] - 128) * 128) + buf[5] - 128; //polozenei x postaci wg serwa
      k = ((buf[6] - 128) * 128) + buf[7] - 128; //y
      l = buf[8] - 128; //grafika postaci (1-8)
      for (n = 1; n < legendscanvas.ilosc_postaci; n++) { //szukanie czy postac jest juz zaladowana
       if (legendscanvas.postac_serw[n] == i) {
        postac_klient = n; //jesli tak to zapamietaj numer pod jakim jest przetrzymywana na kliencie
        break;
       }
      }
      if (postac_klient == -1) { // jesli nie jest zaladowana dodaj ja i zapisz jaki numer bedzie miala na kliencie (te numery moga byc rozne)
       postac_klient = legendscanvas.ilosc_postaci;
       legendscanvas.postac_serw[legendscanvas.ilosc_postaci] = i; //legendscanvas.postac_serw[numer_postaci_na_kliencie]=numer_posraci_na_servie;
       switch (i / 4096) {
        case 0:
         legendscanvas.postac[legendscanvas.ilosc_postaci] = new TiledLayer(1, 1, legendscanvas.postacie, legendscanvas.klocki, legendscanvas.klocki * 2);
         legendscanvas.napisy[legendscanvas.ilosc_postaci] = new TiledLayer(10, 1, legendscanvas.fontbw, 4, 6);
         legendscanvas.napisy_cien[legendscanvas.ilosc_postaci] = new TiledLayer(10, 1, legendscanvas.fontblack, 4, 6);
         break;
        case 1:
         legendscanvas.postac[legendscanvas.ilosc_postaci] = new TiledLayer(1, 1, legendscanvas.postacie, legendscanvas.klocki, legendscanvas.klocki * 2);
         legendscanvas.napisy[legendscanvas.ilosc_postaci] = new TiledLayer(10, 1, legendscanvas.fontlblue, 4, 6);
         legendscanvas.napisy_cien[legendscanvas.ilosc_postaci] = new TiledLayer(10, 1, legendscanvas.fontblack, 4, 6);
         break;
        case 2:
         legendscanvas.postac[legendscanvas.ilosc_postaci] = new TiledLayer(1, 1, legendscanvas.mobs, legendscanvas.klocki, legendscanvas.klocki * 2);
         legendscanvas.napisy[legendscanvas.ilosc_postaci] = new TiledLayer(10, 1, legendscanvas.fontred, 4, 6);
         legendscanvas.napisy_cien[legendscanvas.ilosc_postaci] = new TiledLayer(10, 1, legendscanvas.fontblack, 4, 6);
         break;
       }
       if (legendscanvas.ilosc_postaci < (legendscanvas.max_il_post - 2))
        legendscanvas.ilosc_postaci++; //postac dodana (ilosc akrywnych postaci na kliencie zwieksza sie o 1)
       for (o = 0; o < 10; o++) {
        legendscanvas.napisy[postac_klient].setCell(0 + o, 0, 32); //ustaw pusta nazwe na wypadek gdyby w transmisji nie bylo nicka
        legendscanvas.napisy_cien[postac_klient].setCell(0 + o, 0, 32); //ustaw pusta nazwe na wypadek gdyby w transmisji nie bylo nicka
       }
      }

      if (buf[3] == 81) { //jesli sa informacje o nicku (Q)
       int poczatek = 0;
       //legendscanvas.napisy[postac_klient].setCell(2, 0, 79+1);
       poczatek = 5 - ((buf[9] - 128) / 2); //dlugosc nicka /2 w celu sprawdzenia jak wycentrowac nick

       for (n = 0; n < 10; n++) {
        legendscanvas.napisy[postac_klient].setCell(0 + n, 0, 32);
        legendscanvas.napisy_cien[postac_klient].setCell(0 + n, 0, 32);
        legendscanvas.napisy_arr[(postac_klient * 10) + n] = 32;
       }
       for (n = 0; n < (buf[9] - 128); n++) {
        legendscanvas.napisy[postac_klient].setCell(0 + poczatek + n, 0, buf[10 + n] + 1);
        legendscanvas.napisy_cien[postac_klient].setCell(0 + poczatek + n, 0, buf[10 + n] + 1);
        legendscanvas.napisy_arr[(postac_klient * 10) + poczatek + n] = buf[10 + n] + 1;
       }
       //jesli jest nick sa tez informacje o hp/mp, jesli postac to mob
       if ((i / 4096) > 0) { //jesli postac to mobek
        if ((legendscanvas.post_hp[postac_klient] < ((buf[22] - 128) * 128) + buf[23] - 128) && (legendscanvas.post_hp[postac_klient] == 0)) {
         legendscanvas.post_x[postac_klient] = 0 - (legendscanvas.klocki * 3);
         legendscanvas.post_y[postac_klient] = 0 - (legendscanvas.klocki * 3);
        }
        legendscanvas.post_lvl[postac_klient] = ((buf[20] - 128) * 128) + buf[21] - 128;
        legendscanvas.post_hp[postac_klient] = ((buf[22] - 128) * 128) + buf[23] - 128;
        legendscanvas.post_max_hp[postac_klient] = ((buf[24] - 128) * 128) + buf[25] - 128;
        legendscanvas.post_mp[postac_klient] = ((buf[26] - 128) * 128) + buf[27] - 128;
        legendscanvas.post_max_mp[postac_klient] = ((buf[28] - 128) * 128) + buf[29] - 128;
       } else {
        legendscanvas.post_lvl[postac_klient] = 99;
        legendscanvas.post_hp[postac_klient] = 50000;
        legendscanvas.post_max_hp[postac_klient] = 50000;
        legendscanvas.post_mp[postac_klient] = 0;
        legendscanvas.post_max_mp[postac_klient] = 0;
       }
      } else {
       //nie rob nic z nickiem
      }
      legendscanvas.post_typ[postac_klient] = l; //ustaw typ grafiki
      legendscanvas.post_x_doc[postac_klient] = j * legendscanvas.klocki; //*legendscanvas.klocki; //pozycja docelowa
      legendscanvas.post_y_doc[postac_klient] = k * legendscanvas.klocki; //*legendscanvas.klocki;
      if ((legendscanvas.post_x_doc[postac_klient] > legendscanvas.post_x[postac_klient] + (2 * legendscanvas.klocki)) || (legendscanvas.post_x_doc[postac_klient] < legendscanvas.post_x[postac_klient] - (2 * legendscanvas.klocki)))
       legendscanvas.post_x[postac_klient] = legendscanvas.post_x_doc[postac_klient]; //jesli dalej niz o 2 kratki postac neich sie po prostu pojawi w meijscu docelowym
      if ((legendscanvas.post_y_doc[postac_klient] > legendscanvas.post_y[postac_klient] + (2 * legendscanvas.klocki)) || (legendscanvas.post_y_doc[postac_klient] < legendscanvas.post_y[postac_klient] - (2 * legendscanvas.klocki)))
       legendscanvas.post_y[postac_klient] = legendscanvas.post_y_doc[postac_klient];

      legendscanvas.postac_mode[postac_klient] = legendscanvas.klocki / 4; //szybkosc postaci to 1/4
      if ((legendscanvas.post_x_doc[postac_klient] > legendscanvas.post_x[postac_klient] + (1 * legendscanvas.klocki)) || (legendscanvas.post_x_doc[postac_klient] < legendscanvas.post_x[postac_klient] - (1 * legendscanvas.klocki)))
       legendscanvas.postac_mode[postac_klient] = legendscanvas.klocki / 2; //ale jesli postac jest dalej niz o 1 kratke od celu to predkosc to 1/2
      if ((legendscanvas.post_y_doc[postac_klient] > legendscanvas.post_y[postac_klient] + (1 * legendscanvas.klocki)) || (legendscanvas.post_y_doc[postac_klient] < legendscanvas.post_y[postac_klient] - (1 * legendscanvas.klocki)))
       legendscanvas.postac_mode[postac_klient] = legendscanvas.klocki / 2;
      for (n = 0; n < 20; n++)
       buf[n] = 0;
     }
     if ((buf[3] == 68)) { // jesli 4 znak trasmisji to D - kasowanie
      postac_klient = -1; //zmienna ktora bedzie ifnormacja pod jakim indeksem jest/bedzie ta postac na kliencie
      i = ((buf[1] - 128) * 128) + buf[2] - 128; //numer postaci na serwie
      l = 128 + 1; //grafika postaci (1-8)

      for (n = 1; n < legendscanvas.ilosc_postaci; n++) { //szukanie czy postac jest zaladowana
       if (legendscanvas.postac_serw[n] == i) {
        postac_klient = n; //jesli tak to zapamietaj numer pod jakim jest przetrzymywana na kliencie
        break;
       }
      }
      //debug="1: "+i+":"+postac_klient+":"+legendscanvas.ilosc_postaci;
      if (postac_klient != -1) { // jesli jest zaladowana skasuj ja i przesun wszystkie inne postacie
       for (n = (postac_klient); n < (legendscanvas.ilosc_postaci); n++) { //przesun wszystkie postacie za ta postacia o 1 do tylu. jesli postac byla ostatnia (nr o 1 mneijszy od il_postaci) petla sie nie wykona
        legendscanvas.post_typ[n] = legendscanvas.post_typ[n + 1];
        legendscanvas.post_x_doc[n] = legendscanvas.post_x_doc[n + 1];
        legendscanvas.post_y_doc[n] = legendscanvas.post_y_doc[n + 1];
        legendscanvas.post_x[n] = legendscanvas.post_x[n + 1];
        legendscanvas.post_y[n] = legendscanvas.post_y[n + 1];
        legendscanvas.postac_mode[n] = legendscanvas.postac_mode[n + 1];
        legendscanvas.postac_serw[n] = legendscanvas.postac_serw[n + 1];
        for (o = 0; o < 10; o++) {
         legendscanvas.napisy_arr[(n * 10) + o] = legendscanvas.napisy_arr[((n + 1) * 10) + o]; //przesuniecie pozycji tablicy z napisami o 1 do tylu
         legendscanvas.napisy[n].setCell(0 + o, 0, legendscanvas.napisy_arr[((n) * 10) + o]); //zaktualizowanie samego wyswietlanego tekstu
         legendscanvas.napisy_cien[n].setCell(0 + o, 0, legendscanvas.napisy_arr[((n) * 10) + o]); //zaktualizowanie samego wyswietlanego tekstu
        }
       }
       legendscanvas.post_x_doc[legendscanvas.ilosc_postaci] = -3;
       legendscanvas.post_y_doc[legendscanvas.ilosc_postaci] = -3;
       legendscanvas.post_x[legendscanvas.ilosc_postaci] = -3;
       legendscanvas.post_y[legendscanvas.ilosc_postaci] = -3;
       legendscanvas.ilosc_postaci--; //postac usunieta
      }
     }
    }

    if ((buf[0] == 77) && (m > 3)) { // jesli pierwszy znak to M - mapa (?)
     i = ((buf[1] - 128) * 128) + buf[2] - 128; //numer planszy jaka trzeba wlaczyc
     j = ((buf[3] - 128) * 128) + buf[4] - 128;
     k = ((buf[5] - 128) * 128) + buf[6] - 128;

     if ((i < 3) && (legendscanvas.nr_planszy == 3)) {
      try {
       if (legendscanvas.midiPlayer != null) {
        legendscanvas.midiPlayer.stop();
       }
      } catch (Exception e) {
       System.err.println(e);
      }
     }
     if ((i == 3)) {
      try {
       if (legendscanvas.midiPlayer != null) {
        legendscanvas.midiPlayer.stop();
       }
      } catch (Exception e) {
       System.err.println(e);
      }
     }

     legendscanvas.zmien_plansze(i, j, k);
     legendscanvas.nr_planszy = i;
    }
    if ((buf[0] == 89) && (m > 3)) { // Y - update o zadaniu
     int gracz_quest_nr = ((buf[1] - 128) * 128) + buf[2] - 128;
     legendscanvas.gracz_quest_dozabicia[gracz_quest_nr] = ((buf[3] - 128) * 128) + buf[4] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][0] = ((buf[5] - 128) * 128) + buf[6] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][1] = ((buf[7] - 128) * 128) + buf[8] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][2] = ((buf[9] - 128) * 128) + buf[10] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][3] = ((buf[11] - 128) * 128) + buf[12] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][4] = ((buf[13] - 128) * 128) + buf[14] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][5] = ((buf[15] - 128) * 128) + buf[16] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][6] = ((buf[17] - 128) * 128) + buf[18] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][7] = ((buf[19] - 128) * 128) + buf[20] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][0] = buf[21] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][1] = buf[22] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][2] = buf[23] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][3] = buf[24] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][4] = buf[25] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][5] = buf[26] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][6] = buf[27] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][7] = buf[28] - 128;
    }
    if ((buf[0] == 90) && (m > 3)) { // Z - pelne info o zadaniu
     int gracz_quest_nr = buf[1] - 128; //numer miejsca q na liscie gracza (0-9)
     legendscanvas.gracz_quest_id[gracz_quest_nr] = ((buf[2] - 128) * 128 * 128) + ((buf[3] - 128) * 128) + buf[4] - 128; //id questa
     legendscanvas.gracz_quest_lvl[gracz_quest_nr] = ((buf[5] - 128) * 128) + buf[6] - 128;
     legendscanvas.gracz_quest_typ[gracz_quest_nr] = ((buf[7] - 128) * 128) + buf[8] - 128;
     legendscanvas.gracz_quest_dozabicia[gracz_quest_nr] = ((buf[9] - 128) * 128) + buf[10] - 128;
     legendscanvas.gracz_quest_moby_rodzaje[gracz_quest_nr] = ((buf[11] - 128) * 128) + buf[12] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][0] = ((buf[13] - 128) * 128) + buf[14] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][1] = ((buf[15] - 128) * 128) + buf[16] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][2] = ((buf[17] - 128) * 128) + buf[18] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][3] = ((buf[19] - 128) * 128) + buf[20] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][4] = ((buf[21] - 128) * 128) + buf[22] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][5] = ((buf[23] - 128) * 128) + buf[24] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][6] = ((buf[25] - 128) * 128) + buf[26] - 128;
     legendscanvas.gracz_quest_moby[gracz_quest_nr][7] = ((buf[27] - 128) * 128) + buf[28] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][0] = buf[29] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][1] = buf[30] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][2] = buf[31] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][3] = buf[32] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][4] = buf[33] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][6] = buf[35] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][5] = buf[34] - 128;
     legendscanvas.gracz_quest_moby_ilosc[gracz_quest_nr][7] = buf[36] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][0] = buf[37] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][1] = buf[38] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][2] = buf[39] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][3] = buf[40] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][4] = buf[41] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][5] = buf[42] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][6] = buf[43] - 128;
     legendscanvas.gracz_quest_moby_ilosc_max[gracz_quest_nr][7] = buf[44] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][0] = buf[45] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][1] = buf[46] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][2] = buf[47] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][3] = buf[48] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][4] = buf[49] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][5] = buf[50] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][6] = buf[51] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][7] = buf[52] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][8] = buf[53] - 128;
     legendscanvas.gracz_quest_oddaj[gracz_quest_nr][9] = buf[54] - 128;
    }
    if ((buf[0] == 82) && (m > 3)) { // R - revive!
     legendscanvas.gracz_hp = 1;
     legendscanvas.gracz_mp = 1;
     legendscanvas.dead_secs = -1;
     legendscanvas.glownychar = 13;
     i = ((buf[1] - 128) * 128) + buf[2] - 128; //numer planszy jaka trzeba wlaczyc
     j = ((buf[3] - 128) * 128) + buf[4] - 128;
     k = ((buf[5] - 128) * 128) + buf[6] - 128;
     legendscanvas.zmien_plansze(i, j, k);
     legendscanvas.nr_planszy = i;
    }
    if ((buf[0] == 87) && (m > 5)) { // jesli pierwszy znak to W (welcome) to transmisja dotyczy pozytywnego logowania
     legendscanvas.zalogowany = 1; //WELCOME
     if ((buf[1] - 128) != legendscanvas.version) {
      legendscanvas.version = (buf[1] - 128);
     }
     i = ((buf[2] - 128) * 128) + buf[3] - 128; //numer planszy jaka trzeba wlaczyc
     j = ((buf[4] - 128) * 128) + buf[5] - 128; //pozycja_x w blokach
     k = ((buf[6] - 128) * 128) + buf[7] - 128; //pozycja_y -||-
     legendscanvas.zmien_plansze(i, j, k);
     legendscanvas.nr_planszy = i;
     legendscanvas.gracz_hp = ((buf[8] - 128) * 128) + buf[9] - 128;
     legendscanvas.gracz_mp = ((buf[10] - 128) * 128) + buf[11] - 128;
     legendscanvas.gracz_hp_max = ((buf[12] - 128) * 128) + buf[13] - 128;
     legendscanvas.gracz_mp_max = ((buf[14] - 128) * 128) + buf[15] - 128;
     legendscanvas.gracz_exp = ((buf[16] - 128) * 128) + buf[17] - 128;
     legendscanvas.gracz_lvl = ((buf[18] - 128) * 128) + buf[19] - 128;
     legendscanvas.postac_serw[0] = ((buf[20] - 128) * 128 * 128) + ((buf[21] - 128) * 128) + (buf[22] - 128) + (32 * 128);
     inventory_pobierz = 2;
     equip_pobierz = 2;
    }
    if ((buf[0] == 73) && (m > 5)) { // I-transmisja o inventory
     for (n = 0; n < 4; n++)
      legendscanvas.inventory_bagi[n] = (buf[n + 1] - 128);
     legendscanvas.inventory_kasa = ((buf[5] - 128) * 128 * 128) + ((buf[6] - 128) * 128) + (buf[7] - 128);
     for (n = 0; n < 75; n++) {
      int inv_arr_temp = ((buf[8 + (n * 3)] - 128) * 128 * 128) + ((buf[8 + (n * 3) + 1] - 128) * 128) + (buf[8 + (n * 3) + 2] - 128);
      if (legendscanvas.inventory_arr[n] != inv_arr_temp) legendscanvas.inv_loaded[n] = 0;
      legendscanvas.inventory_arr[n] = inv_arr_temp;
     }
     inv_wyslalem = 0;
     inventory_pobierz = 0;
    }
    if ((buf[0] == 74) && (m > 5)) { // J-transmisja grafik inventory
     switch (buf[1]) {
      case 128:
       for (n = 0; n < 75; n++)
        legendscanvas.inv_grafika[n] = ((buf[2 + (n * 2) + 0] - 128) * 128) + (buf[2 + (n * 2) + 1] - 128);
       break;
      case 129:
       for (n = 0; n < 75; n++)
        legendscanvas.inventory_arr_ilosc[n] = ((buf[2 + (n * 2) + 0] - 128) * 128) + (buf[2 + (n * 2) + 1] - 128);
       break;
     }
    }
    if ((buf[0] == 69) && (m > 5)) { // E-transmisja o equipie
     for (n = 0; n < 30; n++) {
      int inv_arr_temp = ((buf[1 + (n * 3)] - 128) * 128 * 128) + ((buf[1 + (n * 3) + 1] - 128) * 128) + (buf[1 + (n * 3) + 2] - 128);
      if (legendscanvas.equip_arr[n] != inv_arr_temp) legendscanvas.equ_loaded[n] = 0;
      legendscanvas.equip_arr[n] = inv_arr_temp;
      legendscanvas.equ_grafika[n] = 0;
     }
     inv_wyslalem = 0;
     equip_pobierz = 0;
    }
    if ((buf[0] == 70) && (m > 5)) { // J-transmisja grafik equipa
     for (n = 0; n < 30; n++) {
      legendscanvas.equ_grafika[n] = ((buf[2 + (n * 2) + 0] - 128) * 128) + (buf[2 + (n * 2) + 1] - 128);
     }
    }
    if ((buf[0] == 76) && (m > 1)) { // L - LOOT
     legendscanvas.odebrano_loot++;
     legendscanvas.odebrano_loot_czas = 20;
     inventory_pobierz = 2;
    }
    if ((buf[0] == 66) && (m > 3)) { // B - buff
     int buff_nr = ((buf[1] - 128) * 128) + (buf[2] - 128);
     int buff_czas = ((buf[3] - 128) * 128) + (buf[4] - 128);
     int buff_op = buf[5];
     if (buf[5] == (128 + 1)) { //128+1 //rozpoczecie
      if (legendscanvas.buff_index < 15) {
       legendscanvas.buff[legendscanvas.buff_index] = buff_nr;
       legendscanvas.buff_time[legendscanvas.buff_index] = (((long)(buff_czas)) * 500) + (System.currentTimeMillis());
       legendscanvas.buff_index++;
      }
     } else {
      for (i = 0; i < legendscanvas.buff_index; i++) { //usuwanie buffa
       if (legendscanvas.buff[i] == buff_nr) {
        legendscanvas.buff[i] = 0;
        legendscanvas.buff_time[i] = 0;
        for (j = i; j < (legendscanvas.buff_index - 1); j++) {
         legendscanvas.buff[j] = legendscanvas.buff[j + 1];
         legendscanvas.buff_time[j] = legendscanvas.buff_time[j + 1];
        }
        legendscanvas.buff_index--;
       }
      }
     }
    }
    if ((buf[0] == 79) && (m > 5)) { // O - info o itemce
     int nr_itemu = ((buf[40] - 128) * 128 * 128) + ((buf[41] - 128) * 128) + (buf[42] - 128);
     for (n = 0; n < 30; n++) {
      if ((nr_itemu == legendscanvas.equip_arr[n]) && (nr_itemu != 0)) {
       legendscanvas.equ_loaded[n] = 1;
       legendscanvas.equ_typ[n] = ((buf[1] - 128) * 128 * 128) + ((buf[2] - 128) * 128) + (buf[3] - 128);
       legendscanvas.equ_wartosc[n] = ((buf[4] - 128) * 128 * 128) + ((buf[5] - 128) * 128) + (buf[6] - 128);
       legendscanvas.equ_dura[n] = ((buf[7] - 128) * 128 * 128) + ((buf[8] - 128) * 128) + (buf[9] - 128);
       legendscanvas.equ_stack[n] = ((buf[10] - 128) * 128 * 128) + ((buf[11] - 128) * 128) + (buf[12] - 128);
       legendscanvas.equ_arm[n] = ((buf[13] - 128) * 128 * 128) + ((buf[14] - 128) * 128) + (buf[15] - 128);
       legendscanvas.equ_md[n] = ((buf[16] - 128) * 128 * 128) + ((buf[17] - 128) * 128) + (buf[18] - 128);
       legendscanvas.equ_bd[n] = ((buf[19] - 128) * 128 * 128) + ((buf[20] - 128) * 128) + (buf[21] - 128);
       legendscanvas.equ_bh[n] = ((buf[22] - 128) * 128 * 128) + ((buf[23] - 128) * 128) + (buf[24] - 128);
       legendscanvas.equ_int[n] = ((buf[25] - 128) * 128 * 128) + ((buf[26] - 128) * 128) + (buf[27] - 128);
       legendscanvas.equ_spi[n] = ((buf[28] - 128) * 128 * 128) + ((buf[29] - 128) * 128) + (buf[30] - 128);
       legendscanvas.equ_sta[n] = ((buf[31] - 128) * 128 * 128) + ((buf[32] - 128) * 128) + (buf[33] - 128);
       legendscanvas.equ_agi[n] = ((buf[34] - 128) * 128 * 128) + ((buf[35] - 128) * 128) + (buf[36] - 128);
       legendscanvas.equ_str[n] = ((buf[37] - 128) * 128 * 128) + ((buf[38] - 128) * 128) + (buf[39] - 128);
       for (j = 0; j < 30; j++) {
        if (buf[43 + j] != 95)
         legendscanvas.equ_nazwa[n][j] = buf[43 + j];
        else
         legendscanvas.equ_nazwa[n][j] = 32;
       }
      }
     }
     for (n = 0; n < 75; n++) {
      if ((nr_itemu == legendscanvas.inventory_arr[n]) && (nr_itemu != 0)) {
       legendscanvas.inv_loaded[n] = 1;
       legendscanvas.inv_typ[n] = ((buf[1] - 128) * 128 * 128) + ((buf[2] - 128) * 128) + (buf[3] - 128);
       legendscanvas.inv_wartosc[n] = ((buf[4] - 128) * 128 * 128) + ((buf[5] - 128) * 128) + (buf[6] - 128);
       legendscanvas.inv_dura[n] = ((buf[7] - 128) * 128 * 128) + ((buf[8] - 128) * 128) + (buf[9] - 128);
       legendscanvas.inv_stack[n] = ((buf[10] - 128) * 128 * 128) + ((buf[11] - 128) * 128) + (buf[12] - 128);
       legendscanvas.inv_arm[n] = ((buf[13] - 128) * 128 * 128) + ((buf[14] - 128) * 128) + (buf[15] - 128);
       legendscanvas.inv_md[n] = ((buf[16] - 128) * 128 * 128) + ((buf[17] - 128) * 128) + (buf[18] - 128);
       legendscanvas.inv_bd[n] = ((buf[19] - 128) * 128 * 128) + ((buf[20] - 128) * 128) + (buf[21] - 128);
       legendscanvas.inv_bh[n] = ((buf[22] - 128) * 128 * 128) + ((buf[23] - 128) * 128) + (buf[24] - 128);
       legendscanvas.inv_int[n] = ((buf[25] - 128) * 128 * 128) + ((buf[26] - 128) * 128) + (buf[27] - 128);
       legendscanvas.inv_spi[n] = ((buf[28] - 128) * 128 * 128) + ((buf[29] - 128) * 128) + (buf[30] - 128);
       legendscanvas.inv_sta[n] = ((buf[31] - 128) * 128 * 128) + ((buf[32] - 128) * 128) + (buf[33] - 128);
       legendscanvas.inv_agi[n] = ((buf[34] - 128) * 128 * 128) + ((buf[35] - 128) * 128) + (buf[36] - 128);
       legendscanvas.inv_str[n] = ((buf[37] - 128) * 128 * 128) + ((buf[38] - 128) * 128) + (buf[39] - 128);
       for (j = 0; j < 30; j++) {
        if (buf[43 + j] != 95)
         legendscanvas.inv_nazwa[n][j] = buf[43 + j];
        else
         legendscanvas.inv_nazwa[n][j] = 32;
       }
      }
     }
    }
    if ((buf[0] == 86) && (m > 5)) { // jesli pierwszy znak to V (refresh) to odswiez dane
     //
     int temp_gracz_hp = ((buf[1] - 128) * 128) + buf[2] - 128;
     if (legendscanvas.gracz_hp > temp_gracz_hp) {
      legendscanvas.dmg[0] = new TiledLayer(6, 1, legendscanvas.fontbw, 4, 6);
      legendscanvas.dmg_arr[0] = 15;
      j = legendscanvas.gracz_hp - temp_gracz_hp;
      String bufor_dmg;
      String odstep = "   ";
      odstep = "";
      if (j < 1000) odstep = " ";
      if (j < 10) odstep = "  ";
      bufor_dmg = "" + odstep + j;
      char[] bufor_dmg_c = bufor_dmg.toCharArray();
      for (n = 0; n < 6; n++) {
       if (bufor_dmg.length() > n)
        legendscanvas.dmg[0].setCell(n, 0, bufor_dmg_c[n] + 1);
       else
        legendscanvas.dmg[0].setCell(n, 0, 32 + 1);
      }
     }
     legendscanvas.gracz_hp = ((buf[1] - 128) * 128) + buf[2] - 128;
     legendscanvas.gracz_mp = ((buf[3] - 128) * 128) + buf[4] - 128;
     legendscanvas.gracz_hp_max = ((buf[5] - 128) * 128) + buf[6] - 128;
     legendscanvas.gracz_mp_max = ((buf[7] - 128) * 128) + buf[8] - 128;
     legendscanvas.gracz_exp = ((buf[9] - 128) * 128) + buf[10] - 128;
     if ((((buf[11] - 128) * 128) + buf[12] - 128) > legendscanvas.gracz_lvl) legendscanvas.lvl_up = 40;
     legendscanvas.gracz_lvl = ((buf[11] - 128) * 128) + buf[12] - 128;
    }
    bajtow = bajtow + m;
   }
   if (legendscanvas.zalogowany == 1) {
    char_array[0] = 67; //c
    int tymcz = 64 + ((legendscanvas.pozycja_x / legendscanvas.klocki) / 64);
    int tymcz2 = 64 + (legendscanvas.pozycja_x / legendscanvas.klocki) - ((tymcz - 64) * 64);
    int tymcz3 = 0;
    char_array[1] = (char)(tymcz);
    char_array[2] = (char)(tymcz2);
    tymcz = 64 + ((legendscanvas.pozycja_y / legendscanvas.klocki) / 64);
    tymcz2 = 64 + (legendscanvas.pozycja_y / legendscanvas.klocki) - ((tymcz - 64) * 64);
    char_array[3] = (char)(tymcz);
    char_array[4] = (char)(tymcz2);
    char_array[5] = 63;
    char_array[6] = 63;
    char_array[7] = 63;
    char_array[8] = 13;
    char_array[9] = 10;
    legendscanvas.message2 = String.valueOf(char_array);
    try {
     legendscanvas.os.write(legendscanvas.message2.getBytes());
    } catch (IOException ioe) {
     ioe.printStackTrace();
    }
    bajtow_sent += 10;

    if (legendscanvas.przerwano_cast > 0) {
     char_array[0] = 65; //A
     char_array[1] = 64 - 1; //A
     char_array[2] = 63;
     char_array[3] = 63;
     char_array[4] = 63;
     char_array[5] = 63;
     char_array[6] = 63;
     char_array[7] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     legendscanvas.przerwano_cast = 0;
    }
    if (legendscanvas.cast > 0) { //jesli zaczeto castowanie wyslij info o ataku
     char_array[0] = 65; //A
     legendscanvas.nr_spella = legendscanvas.cast_nr[legendscanvas.cast - 1];
     tymcz = 64 + (legendscanvas.nr_spella / 64);
     tymcz2 = 64 + (legendscanvas.nr_spella) - ((tymcz - 64) * 64);
     char_array[1] = (char) tymcz; //legendscanvas.nr_spella
     char_array[2] = (char) tymcz2;
     if (legendscanvas.nr_target > -1) {
      tymcz = 64 + (legendscanvas.nr_target / (64 * 64));
      tymcz2 = 64 + ((legendscanvas.nr_target - ((tymcz - 64) * (64 * 64))) / 64);
      tymcz3 = 64 + legendscanvas.nr_target - ((tymcz2 - 64) * 64) - ((tymcz - 64) * (64 * 64));
     } else {
      legendscanvas.nr_target = legendscanvas.postac_serw[0];
      tymcz = 64 + (legendscanvas.nr_target / (64 * 64));
      tymcz2 = 64 + ((legendscanvas.nr_target - ((tymcz - 64) * (64 * 64))) / 64);
      tymcz3 = 64 + legendscanvas.nr_target - ((tymcz2 - 64) * 64) - ((tymcz - 64) * (64 * 64));
      legendscanvas.nr_target = -1;
     }
     char_array[3] = (char)(tymcz); //nr mobka w ktorego uderzamy spellem
     char_array[4] = (char)(tymcz2);
     char_array[5] = (char)(tymcz3);
     char_array[6] = 63;
     char_array[7] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     for (i = 0; i < (legendscanvas.cast - 2); i++) {
      legendscanvas.cast_nr[i] = legendscanvas.cast_nr[i + 1];
      //cast_loc_nr[i]=cast_loc_nr[i+1];
     }
     legendscanvas.cast--;
    }
    if (legendscanvas.dead_secs == 1) { //jesli gracz sie revivnal
     char_array[0] = 82; //R
     for (i = 1; i < 8; i++)
      char_array[i] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     legendscanvas.dead_secs = 0;
    }
    if (legendscanvas.uzyj_item == 1) { //uzyj itemki 2-wyslano 0- odebrano
     char_array[0] = 85; //U
     for (i = 1; i < 8; i++)
      char_array[i] = 63;
     tymcz = 64 + legendscanvas.menu_x;
     tymcz2 = 64 + legendscanvas.menu_y;
     char_array[1] = (char) tymcz;
     char_array[2] = (char) tymcz2;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     legendscanvas.uzyj_item = 0;
     inventory_pobierz = 2;
     equip_pobierz = 2;
    }
    if (legendscanvas.discard_item == 1) { //usun itemke/i 2-wyslano 0- odebrano
     char_array[0] = 86; //V
     for (i = 1; i < 8; i++)
      char_array[i] = 63;
     tymcz = 64 + legendscanvas.menu_x;
     tymcz2 = 64 + legendscanvas.menu_y;
     char_array[1] = (char) tymcz;
     char_array[2] = (char) tymcz2;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     legendscanvas.discard_item = 0;
     inventory_pobierz = 2;
     //equip_pobierz=2;
    }
    if (legendscanvas.podnies_loot == 1) {
     char_array[0] = 76; //L
     for (i = 1; i < 8; i++)
      char_array[i] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     legendscanvas.podnies_loot = 0;
     //inventory_pobierz=2;
    }
    if (inventory_pobierz == 2) { //2-rzadanie pobrania 1- wyslano rzadanie 0-inventory zaktualizowane
     char_array[0] = 73; //I
     for (i = 1; i < 8; i++)
      char_array[i] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     inventory_pobierz = 1;
    }
    if (equip_pobierz == 2) { //2-rzadanie pobrania 1- wyslano rzadanie 0-equip zaktualizowany
     char_array[0] = 69; //E
     for (i = 1; i < 8; i++)
      char_array[i] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     equip_pobierz = 1;
    }
    if ((legendscanvas.inv_zaladuj > 0) && (legendscanvas.inv_zaladuj != inv_wyslalem)) { //wyslij zapytanie o przedmiot
     char_array[0] = 79; //O
     tymcz = 64 + (legendscanvas.inv_zaladuj / (64 * 64));
     tymcz2 = 64 + ((legendscanvas.inv_zaladuj - ((tymcz - 64) * (64 * 64))) / 64);
     tymcz3 = 64 + legendscanvas.inv_zaladuj - ((tymcz2 - 64) * 64) - ((tymcz - 64) * (64 * 64));
     char_array[1] = (char)(tymcz); //numer itemu na temat ktorego zadamy info
     char_array[2] = (char)(tymcz2);
     char_array[3] = (char)(tymcz3);
     char_array[4] = 63;
     char_array[5] = 63;
     char_array[6] = 63;
     char_array[7] = 63;
     char_array[8] = 13;
     char_array[9] = 10;
     legendscanvas.message2 = String.valueOf(char_array);
     try {
      legendscanvas.os.write(legendscanvas.message2.getBytes());
     } catch (IOException ioe) {
      ioe.printStackTrace();
     }
     bajtow_sent += 10;
     inv_wyslalem = legendscanvas.inv_zaladuj;
    }
   }
   legendscanvas.debug = "" + ((((bajtow) + (bajtow_sent)) * 2) / 1024) + "kb";
   legendscanvas.debug = legendscanvas.debug;
  } //koniec comm()

}
