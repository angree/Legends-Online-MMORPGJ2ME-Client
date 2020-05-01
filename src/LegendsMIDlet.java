import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;

public class LegendsMIDlet
extends MIDlet
implements CommandListener {
 private Display dgDisplay;
 private LegendsCanvas hdCanvas;


 static final Command ExitCommand = new Command("Exit", Command.EXIT, 0);

 public LegendsMIDlet() {
  dgDisplay = Display.getDisplay(this);
 }

 protected void startApp()
 {
  try {
   hdCanvas = new LegendsCanvas(this);
   hdCanvas.start();
   hdCanvas.addCommand(ExitCommand);
   hdCanvas.setCommandListener(this);
  } catch (IOException ioe) {
   System.err.println("Problem loading image " + ioe);
  }

  dgDisplay.setCurrent(hdCanvas);
 }

 public void pauseApp() {}

 public void destroyApp(boolean unconditional) {
  hdCanvas.stop();
 }
 public void commandAction(Command c, Displayable s) {
  if (c.getCommandType() == Command.EXIT) {
   destroyApp(true);
   notifyDestroyed();
  }
 }




}




class LegendsCanvas
extends GameCanvas
implements Runnable {

  public int max_il_post = 100;

  public InputStream is;
  public OutputStream os;
  public SocketConnection sc;
  public int ilosc_postaci = 1;
  public int[] postac_serw = new int[max_il_post];
  public int exp_wyswietl = 0;
  public int exp_wartosc = 0;

  private TextBox textBox;

  private Display display;
  private Form f;
  private StringItem si;
  private TextField tf;
  private boolean stop;
  private Command sendCommand = new Command("Send", Command.ITEM, 1);
  private Command exitCommand = new Command("Exit", Command.EXIT, 1);

  ProcedureCLASS procedureclass;
  CommCLASS commclass;

  public String message2;
  private String address2;

  public int version = 26;
  String wersja = "V 0.26 2010-10-28";
  long czas1 = 0;
  long czas2 = 0;
  long czas3 = 0;
  int frameskip = 0; //przy 20fps 0, 10fps - 1, 5fps - 2, 2,5fps - 3

  int gr;
  int timeStep = 50;
  int timeStep2 = 50;

  long cast_start = 0;
  long cast_end = 0;
  long cast_time = 0;

  int kierunek = 0; //0 - nic    1-gora 2-prawo 3-dol 4-lewo
  int kierunek_c = 0; // jesli kierunek jest wcisniety nic tej zmiennej nie zablokuje
  int kierunek_buf = 0; //0 - nic    1-gora 2-prawo 3-dol 4-lewo
  int kierunek_pamiec = 0; //0 - nic    1-gora 2-prawo 3-dol 4-lewo
  int fire = 0; //>0 wcisniety
  int fire_buf = 0; //>0 wcisniety

  int firea = 0;
  int firea_buf = 0;
  int fireb = 0;
  int fireb_buf = 0;
  int firec = 0;
  int firec_buf = 0;
  int fired = 0;
  int fired_buf = 0;

  public int odebrano_loot = 0;
  public int odebrano_loot_czas = 0;
  int klatka = 0;
  int klatka_sek = 0;

  int bufor = 0; // 1-6 robot jest w buforze teleportacji od 1 do 6; 0 - bufor pusty
  int bufor_kier = 0; // kierunek robota wchodzacego do teleportu
  int bufor_x = 0; // wspolrzedne teleportu wejsciowego - ustawiane "niemozliwe" liczby po "okrazeniu" planszy
  int bufor_y = 0; // (zapobiega teleportacji do tego samego teleportu jesli istnieja inne)

  int koniec_casta = 0; //infor o klatce animacji. 1-wlasnie postac skonczyla casta, dodaj klatke animacji
  int poz_key_x = 0; //od klawiatury
  int poz_key_y = 0;
  int key_szer = 10;
  int[] key_input = new int[100];
  int key_pozycja = 0;
  int na_klawiszu = 0;
  int cykl = 0;

  int i, j;
  int k = 0;
  int l = 0;
  int l2 = 0;
  int m = 0;
  int n = 0;
  int o = 0;
  int p = 0;
  String debug = "nic";
  public String debug2 = "";

  int keyStates2 = 0;
  int nr_klawiatury = 0;
  int naboje = 0;
  int klucze = 0;
  int srubki = 0;
  int zycia = 0;
  int kapsula = 0;
  int kapsula_o = 0;
  int koniec_poziomu = 0;
  int lvl; //level startowy
  int max_lvl = 7;
  int restart = 0;
  int pos_y = 0;
  int posdoc_y = 0;
  public long dead_secs = -1; //-1 - nie zaczal odliczac (zyje) >0 - czeka na resa  0 - moze sie resnac
  long dead_secs_end = 0;
  long dead_secs_start = 0;

  int buforuj_fire = 0;
  int bufor_f = 0;

  int klatka_anim = 0; //0-2 klatka animacji wody
  public int klocki = 16;
  int wysokosc = 10;
  int szerokosc = 10;
  int czestotliwosc = 12;
  int kamera = 0; //umieszczenie kamery w osi x (co do pixela, mnozyc przez zm. klocki)

  int robot_y = 0; //aktualna pozycja robota (y)
  int robot_x = 0;


  public int przerwano_cast = 0;
  public int lvl_up = 0;


  int[] wolne = new int[5]; //do badania wolnej drogi teleportacji docelowej


  int[] nick_gracza = new int[10];

  int[] mapa = new int[1200];
  int[] plansza = new int[10000];
  int[] plansza_l2 = new int[10000];
  int[] plansza_l3 = new int[10000];
  int[] plansza_l4 = new int[10000];

  int plansza_x = 80; //dla mapy 1
  int plansza_y = 40;
  //	int plansza_x=20;  //dla mapy 1
  //	int plansza_y=20;
  public int nr_planszy = 1;
  int kamera_x = 0;
  int kamera_y = 0;

  int IL_QUESTOW = 10;

  public int[] gracz_quest_id = new int[IL_QUESTOW]; //ID questa w bazie - zeby zczytac tekst q
  public int[] gracz_quest_lvl = new int[IL_QUESTOW]; //lvl wymagany dla qesta. 0 - brak wymagan <0 - na przyszlosc (inne parametry, q klasowe, etc.)
  public int[][] gracz_quest_oddaj = new int[IL_QUESTOW][11]; //nazwa npc ktory moze przyjac zakonczony quest
  public int[] gracz_quest_typ = new int[IL_QUESTOW]; //1-zabijanie mobow
  public int[] gracz_quest_dozabicia = new int[IL_QUESTOW]; //ilosc mobkow/przedmiotow do zebrania (ogolnie), jesli 0 to licza sie szczegolowe rzadania
  public int[] gracz_quest_moby_rodzaje = new int[IL_QUESTOW]; //ilosc rodzajow mobow/przedmiotow ktore nalezy zebrac/zabic (max 8)
  public int[][] gracz_quest_moby = new int[IL_QUESTOW][8]; //id mobow/przedmiotow ktore nalezy zebrac/zabic (max 8)
  public int[][] gracz_quest_moby_ilosc = new int[IL_QUESTOW][8]; //ilosc poszczegolnych mobow/przedmiotow ktore sa juz zabite
  public int[][] gracz_quest_moby_ilosc_max = new int[IL_QUESTOW][8]; //ilosc poszczegolnych mobow/przedmiotow ktore nalezy zebrac/zabic

  public int gracz_hp;
  public int gracz_mp;
  public int gracz_hp_max;
  public int gracz_mp_max;
  public int gracz_lvl;
  public int gracz_exp;

  int pozycja_x = 37 * klocki;
  int pozycja_y = 0;

  public int pozycja_x_doc = 0;
  public int pozycja_y_doc = 0;

  public int zalogowany = 0;


  int[] poziom_shadow = new int[1120];
  int[] poziom_shadow2 = new int[1120];

  int[] poziom_temp = new int[1120];
  int[] poziom_temp2 = new int[1120];


  int[] tab_klatki = new int[8];
  int nr_klatki = 0;

  public int glownychar = 13; //pierwsza klatka
  int k_odb = 0;

  public int nr_spella = 1;
  public int nr_target = -1; //4096+74;
  int nr_target_loc = -1;

  public int cast = 0; //0 - 3 - ilosc spelli
  public int[] cast_nr = new int[3];

  int cast_loc = 0; //0 - 3 - ilosc spelli
  int[] cast_loc_nr = new int[3];

  public int uzyj_item = 0; //0 - nic nie uzywaj 1-wyslij info o uzyciu 2- wyslano  -> zeruje przy odebraniu
  public int discard_item = 0; //0 - nic nie uzywaj 1-wyslij info o uzyciu 2- wyslano  -> zeruje przy odebraniu

  int ilosc_linii_inv = 0;

  private LegendsMIDlet midlet;
  private Sprite carSprite;
  private Sprite tloSprite;

  private Player player;

  private Player s01;
  private Player s02;
  private Player s03;
  private Player s04;
  private Player s05;
  private Player s06;
  private Player s07;
  private Player s08;
  private Player s09;

  LayerManager layerManager;


  private boolean gameRunning;
  private boolean Gra;
  private boolean collision = false;

  private int width;
  private int height;

  public  int menu_x = 0;
  public  int menu_y = 0;
  private int menu_temp_x = 0;
  private int menu_temp_y = 0;

  private int discard_x = 0;

  private long gameDuration;

  private Image tilesy;
  private TiledLayer mapa_blokow;

  private Image spele;
  private TiledLayer spele_b;

  private Image buffy;
  private TiledLayer buffy_b;
  private Image buffy_sm;

  private TiledLayer spele_czas;
  private TiledLayer spele_pokaz;

  private Image chmury;
  private int cykl_chmury;

  private Image itemy; //itemy do inventory
  private TiledLayer itemy_inv;
  private TiledLayer itemy_inv_ilosc;
  private TiledLayer[] nap_itemy = new TiledLayer[20];


  private int logo = 0;

  public int inv_zaladuj = 0; //numer itemu

  public int[] inv_loaded = new int[5 * 15]; //0 - nie zaladowane 1 - zaladowane

  public int[][] inv_nazwa = new int[5 * 15][31];

  public  int[] inv_grafika = new int[5 * 15];
  public  int[] inv_typ = new int[5 * 15];
  public  int[] inv_wartosc = new int[5 * 15];
  public  int[] inv_dura = new int[5 * 15];
  public  int[] inv_stack = new int[5 * 15];

  public  int[] inv_arm = new int[5 * 15];
  public  int[] inv_md = new int[5 * 15];
  public  int[] inv_bd = new int[5 * 15];
  public  int[] inv_bh = new int[5 * 15];

  public  int[] inv_int = new int[5 * 15];
  public  int[] inv_spi = new int[5 * 15];
  public  int[] inv_sta = new int[5 * 15];
  public  int[] inv_agi = new int[5 * 15];
  public  int[] inv_str = new int[5 * 15];


  public  int[] equ_loaded = new int[2 * 15]; //0 - nie zaladowane 1 - zaladowane

  public  int[][] equ_nazwa = new int[2 * 15][31];

  public  int[] equ_grafika = new int[2 * 15];
  public  int[] equ_typ = new int[2 * 15];
  public  int[] equ_wartosc = new int[2 * 15];
  public  int[] equ_dura = new int[2 * 15];
  public  int[] equ_stack = new int[2 * 15];

  public  int[] equ_arm = new int[2 * 15];
  public  int[] equ_md = new int[2 * 15];
  public  int[] equ_bd = new int[2 * 15];
  public  int[] equ_bh = new int[2 * 15];

  public  int[] equ_int = new int[2 * 15];
  public  int[] equ_spi = new int[2 * 15];
  public  int[] equ_sta = new int[2 * 15];
  public  int[] equ_agi = new int[2 * 15];
  public  int[] equ_str = new int[2 * 15];
  public int[] equip_arr = new int[2 * 15];


  public  int buff_index = 0;
  private int quest_title_bytes = 0;
  public int[] buff = new int[16];
  public long[] buff_time = new long[16];
  char[] info_c = "                               ".toCharArray();
  char[] tytul_quest = new char[100];
  char[] tresc_quest = new char[1000];
  char[] info_quest = new char[2700];
  private int ilosc_linii_q = 0;
  private int pozycja_linii_q = 0;
  private int ogladany_q = 0;
  private Image inventory;
  private TiledLayer inventory_b;
  private TiledLayer inventory_menu;
  private Image item_info;
  private TiledLayer item_info_b;
  public int[] inventory_arr = new int[5 * 15];
  //public int [] inventory_arr_gfx = new int[5*15];
  public int[] inventory_arr_ilosc = new int[5 * 15];
  public int[] inventory_bagi = new int[5]; //bagi
  public int inventory_cap = 15; //pojemnosc
  public int inventory_kasa = 0; //kasa
  public int inventory_pokaz = 0; //pokaz inv
  private int discard_pokaz = 0; //pokaz inv
  private int menu_pokaz = 0; //pokaz menu
  private int char_pokaz = 0; //pokaz okno chara
  private int quest_pokaz = 0; //pokaz okno z questami
  private int quest_menu_nr = 1; //nr pokazywanego q w menu z questami
  private int quest_loaded = 0;
  public int podnies_loot = 0;

  private Image canvas01;
  private TiledLayer canvas01_b;

  private Image canvas02;
  private TiledLayer canvas02_b;

  private Image canvas03;
  private TiledLayer canvas03_b;

  private TiledLayer canvas04_b;

  public Image postacie;
  public Image mobs;

  private TiledLayer postacie_b;
  private TiledLayer postac_g;

  private Image fontm;
  public Image fontbw;
  public Image fontblack;
  public Image fontred;
  public Image fontlblue;
  private Image fontblue;
  private Image fontgreen;
  private Image fontgray;
  private Image fontviolet;
  private Image fontyellow;
  private TiledLayer font_m;
  private TiledLayer font_t;
  private TiledLayer buff_napis;

  private Image pasek;
  private TiledLayer pasek_en;

  private Image kursor;
  private Image kursor16;
  private TiledLayer kursor_b;
  private TiledLayer kursor_menu;

  //deklaracja postaci i fontow

  private TiledLayer napis_klawiatura;

  private TiledLayer[] numerki_lvl = new TiledLayer[7]; // numerki lvlow (5 dla druzyny i 6-ty dla targetu)
  private TiledLayer[] hp_mp = new TiledLayer[7]; // hp/mp graczy (5 dla druzyny i 6-ty dla targetu) 7 - dla expa

  public TiledLayer[] postac = new TiledLayer[max_il_post];
  public TiledLayer[] napisy = new TiledLayer[max_il_post];
  public TiledLayer[] napisy_cien = new TiledLayer[max_il_post];
  public TiledLayer[] dmg = new TiledLayer[max_il_post];
  public int[] dmg_arr = new int[max_il_post];
  public int[] napisy_arr = new int[(max_il_post + 1) * 10];
  public int[] napisy_arr_d = new int[max_il_post + 1];
  public int[] post_x = new int[max_il_post];
  public int[] post_y = new int[max_il_post];
  public int[] post_x_doc = new int[max_il_post];
  public int[] post_y_doc = new int[max_il_post];
  public int[] post_kierunek = new int[max_il_post];
  public int[] post_kierunek_pamiec = new int[max_il_post];
  public int[] post_typ = new int[max_il_post];
  public int[] post_hp = new int[max_il_post];
  public int[] post_mp = new int[max_il_post];
  public int[] post_max_hp = new int[max_il_post];
  public int[] post_max_mp = new int[max_il_post];
  public int[] post_lvl = new int[max_il_post];
  public int[] postac_mode = new int[max_il_post];
  public int[] post_x_pocz = new int[max_il_post];
  public int[] post_y_pocz = new int[max_il_post];



  private Image glowna;
  private Image gamelogo;
  private Image oskey;

  private Image pasek_dol;
  private Image pasek_dol2;
  private Image pasek_postac_1;
  private Image pasek_postac_2;

  public Player midiPlayer = null;

  public int keyStates = 0;
  public int keyStates3 = 0;
  public int keyStates4 = 0;

  public int spell_serw_info = -1;
  public int spell_serw_info_pozycja = 0;
  public int[] spell_serw_info_bufor = new int[50];


 public LegendsCanvas(LegendsMIDlet lomidlet)
 throws IOException {
  super(false);
  setFullScreenMode(true);

  this.midlet = lomidlet;


  layerManager = new LayerManager();

  width = getWidth();
  height = getHeight();

  layerManager.setViewWindow(0, 0, width, height);
 }


 public void start() {
  gameRunning = true;
  Thread gameThread = new Thread(this);
  gameThread.start();
 }

 public void stop() {
  gameRunning = false;
 }

 public void run() //petla glowna
 {


  while (1 == 1) {
   width = getWidth();
   height = getHeight();


   lvl = 1;

   klocki = 16; //szerokosc/wysokosc klockow w pikeslach

   wysokosc = (height / klocki) - 2; //ilosc tilesow ile sie miesci na ekranie
   szerokosc = (width / klocki) - 1; //j.w.
   //wysokosc = 9;

   nr_klawiatury = 0;
   if (width > 175) nr_klawiatury = 1;
   if (width > 239) nr_klawiatury = 2;

   switch (nr_klawiatury) {
    case 0:
     try {
      oskey = Image.createImage("/key/on_screen_key_110.png");
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
    case 1:
     try {
      oskey = Image.createImage("/key/on_screen_key_176.png");
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
    case 2:
     try {
      oskey = Image.createImage("/key/on_screen_key_220.png"); //220
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
   }

   if ((width == 480) && (height == 272)) nr_klawiatury = 3;

   switch (nr_klawiatury) {
    case 0:
     try {
      logo = 340;
      glowna = Image.createImage("/logo/castle_340x220.jpg");
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
    case 1:
     try {
      logo = 340;
      glowna = Image.createImage("/logo/castle_340x220.jpg");
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
    case 2:
     try {
      logo = 480;
      glowna = Image.createImage("/logo/castle_480x320.jpg");
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
    case 3:
     try {
      logo = 480;
      glowna = Image.createImage("/logo/castle_480x272.jpg");
     } catch (IOException ioe) {
      System.err.println("Problem loading image " + ioe);
     }
     break;
   }

   if (nr_klawiatury == 3) nr_klawiatury = 2;

   try {
    gamelogo = Image.createImage("/logo/legends_online_logo_160_3.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image " + ioe);
   }

   try {
    pasek_dol = Image.createImage("/tiles/pasek_nowy2.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image " + ioe);
   }

   try {
    pasek_dol2 = Image.createImage("/tiles/pasek_nowy.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image " + ioe);
   }






   Graphics g = getGraphics();
   g.setColor(0, 0, 0);
   g.fillRect(0, 0, getWidth(), getHeight()); //wypelnienie ekranu kolorem 0,0,0
   flushGraphics(); //zamiana bufora graficznego (uwidacznia zmiany na ekranie)


   keyStates2 = getKeyStates();
   int pozycja = 0;


   while ((((keyStates & FIRE_PRESSED) == 0) && ((keyStates2 & FIRE_PRESSED) == 0))) { //strona glowna.  2 sposoby zczytywania klawiszy. w sumie 1 mozna sobie darowac bo dotyczy tylko paru telefonow jak siemens s65 (mialem go dlatego to tu jest)
    g.setColor(0, 0, 0);
    g.fillRect(0, 0, getWidth(), getHeight()); //wypelnianie obsszaru ustawionym kolorem
    g.drawImage(glowna, 0, 0, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.setColor(63, 191, 255);
    g.setColor(255, 63, 0);
    g.drawString(wersja, 0, 0, Graphics.TOP | Graphics.LEFT);
    flushGraphics(); //przerzucenie tego co narysowalem z tylu do przodu (double buffor)

    try {
     Thread.sleep(20); //- duration); //bez sleepa chocby na krotki czas program sie zatnie i nie bedzie w stanie zczytac zadnego klwisza. sleep w kazdej petli musi byc w ktorej chcesz klawisze czytac i tez "try {" obowiazkowy
    } catch (InterruptedException ie) {
     stop();
    }
    keyStates2 = getKeyStates();

   }


   g.setColor(0, 0, 0);
   g.drawString("Loading Clouds ...", 0, 0, Graphics.TOP | Graphics.LEFT);
   flushGraphics();


   g.drawString("Loading gfx Layer1...", 0, 1 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   try {
    canvas01 = Image.createImage("/tiles/!mapa001_l123.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image " + ioe);
   }

   g.drawString("Loading gfx Layer2...", 0, 2 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();


   g.drawString("Loading gfx chars...", 0, 3 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   try {
    postacie = Image.createImage("/tiles/postacie16_przezroczyste.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image " + ioe);
   }

   try {
    mobs = Image.createImage("/tiles/postacie16_mob_01.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image " + ioe);
   }

   g.drawString("Loading fonts...", 0, 4 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();


   try {
    fontbw = Image.createImage("/tiles/czcionki4x6_bw.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }

   try {
    fontblack = Image.createImage("/tiles/czcionki4x6_black.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }

   try {
    fontred = Image.createImage("/tiles/czcionki4x6_red.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }

   try {
    fontlblue = Image.createImage("/tiles/czcionki4x6_lblue.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }
   try {
    fontblue = Image.createImage("/tiles/czcionki4x6_blue.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }
   try {
    fontgray = Image.createImage("/tiles/czcionki4x6_gray.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }
   try {
    fontviolet = Image.createImage("/tiles/czcionki4x6_violet.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }
   try {
    fontyellow = Image.createImage("/tiles/czcionki4x6_yellow.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }
   try {
    fontgreen = Image.createImage("/tiles/czcionki4x6_green.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading small fonts " + ioe);
   }


   g.drawString("Loading extra elements...", 0, 5 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   try {
    pasek = Image.createImage("/tiles/pasek_energii3.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }

   try {
    kursor = Image.createImage("/tiles/kursor_czerwony.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }

   try {
    kursor16 = Image.createImage("/tiles/kursor16_czerwony.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }

   try {
    spele = Image.createImage("/spells.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }

   try {
    buffy = Image.createImage("/buffs.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }
   try {
    buffy_sm = Image.createImage("/buffs_sm.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }

   try {
    pasek_postac_1 = Image.createImage("/tiles/postac_1.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }
   try {
    pasek_postac_2 = Image.createImage("/tiles/postac_2.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }

   try {
    inventory = Image.createImage("/panel/inventory_07.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }
   try {
    item_info = Image.createImage("/panel/item_info.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image pasek" + ioe);
   }
   try {
    itemy = Image.createImage("/tiles/ikony12_v3.png");
   } catch (IOException ioe) {
    System.err.println("Problem loading image ikony" + ioe);
   }


   g.drawString("Creating L1 MAP", 0, 6 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   canvas01_b = new TiledLayer(szerokosc + 3, wysokosc + 3, canvas01, klocki, klocki); //tlo
   spele_b = new TiledLayer(3, 1, spele, 12, 12); //spelle do castowania
   buffy_b = new TiledLayer(8, 2, buffy, 6, 6); //buffy
   spele_czas = new TiledLayer(3, 1, fontbw, 4, 6); //czas
   spele_pokaz = new TiledLayer(3, 3, spele, 12, 12); //panel ze spellami
   inventory_b = new TiledLayer(12, 17, inventory, 12, 12);
   inventory_menu = new TiledLayer(5, 1, inventory, 12, 12);
   inventory_menu.setCell(0, 0, 42);
   inventory_menu.setCell(1, 0, 41);
   inventory_menu.setCell(2, 0, 40);
   inventory_menu.setCell(3, 0, 39);
   inventory_menu.setCell(4, 0, 38);
   item_info_b = new TiledLayer(14, 9, item_info, 12, 12);
   itemy_inv = new TiledLayer(5, 15, itemy, 12, 12);
   itemy_inv_ilosc = new TiledLayer(5 * 3, 15 * 2, fontbw, 4, 6);

   g.drawString("                      L2", 0, 6 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   canvas02_b = new TiledLayer(szerokosc + 3, wysokosc + 3, canvas01, klocki, klocki); //layer 2

   g.drawString("                              L3...", 0, 6 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   canvas03_b = new TiledLayer(szerokosc + 3, wysokosc + 3, canvas01, klocki, klocki); //layer 3

   canvas04_b = new TiledLayer(szerokosc + 3, wysokosc + 3, canvas01, klocki, klocki); //layer 4

   g.drawString("Creating PLRchar and fonts...", 0, 7 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   postacie_b = new TiledLayer(szerokosc + 3, wysokosc + 3, postacie, klocki, klocki * 2); //postacie
   postac_g = new TiledLayer(1, 1, postacie, klocki, klocki * 2); //postac glowna

   font_m = new TiledLayer(10, 1, fontbw, 4, 6); //nick postaci
   font_t = new TiledLayer(10, 1, fontbw, 4, 6); //napis nicka targeta

   buff_napis = new TiledLayer(3, 1, fontbw, 4, 6);

   pasek_en = new TiledLayer(2, 10, pasek, 54, 12); //pasek energii

   kursor_b = new TiledLayer(1, 1, kursor16, klocki, klocki);
   kursor_menu = new TiledLayer(1, 1, kursor, 12, 12);

   g.drawString("Init chars and writing...", 0, 8 * (height / 12), Graphics.TOP | Graphics.LEFT);
   flushGraphics();

   for (i = 0; i < max_il_post; i++) {
    postac[i] = new TiledLayer(1, 1, postacie, klocki, klocki * 2);
   }


   for (i = 0; i < 7; i++) {
    numerki_lvl[i] = new TiledLayer(2, 1, fontbw, 4, 6);
    hp_mp[i] = new TiledLayer(12, 2, fontbw, 4, 6);
   }

   //gracz 0 - nasza postac
   napisy[0] = new TiledLayer(10, 1, fontbw, 4, 6);
   napisy_cien[0] = new TiledLayer(10, 1, fontblack, 4, 6);
   dmg[0] = new TiledLayer(6, 1, fontbw, 4, 6);
   dmg_arr[0] = 0; //wszystkie napisy niewidoczne
   for (i = 1; i < max_il_post; i++) {
    napisy[i] = new TiledLayer(10, 1, fontbw, 4, 6);
    napisy_cien[i] = new TiledLayer(10, 1, fontblack, 4, 6);
    dmg[i] = new TiledLayer(6, 1, fontbw, 4, 6);

    dmg_arr[i] = 0; //wszystkie napisy niewidoczne
   }
   for (i = 0; i < 14; i++)
    nap_itemy[i] = new TiledLayer(30, 1, fontbw, 4, 6);

   napis_klawiatura = new TiledLayer(32, 3, fontbw, 4, 6);


   Gra = true;
   zycia = 99;
   while (Gra) {
    g.drawString("Loading maps from files...", 0, 9 * (height / 12), Graphics.TOP | Graphics.LEFT);
    flushGraphics();

    zaladujmape("/level/map_001_l4.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l4[i] = plansza[i];
    }
    zaladujmape("/level/map_001_l3.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l3[i] = plansza[i];
    }
    zaladujmape("/level/map_001_l2.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l2[i] = plansza[i];
    }
    zaladujmape("/level/map_001_l1.dat");
    //start poziomu

    try {
     if (midiPlayer != null) {
      midiPlayer.stop();
     }
    } catch (Exception e) {
     System.err.println(e);
    }





    g.drawString("Zaladowano.", 0, 10 * (height / 12), Graphics.TOP | Graphics.LEFT);
    flushGraphics();

    long startTime = System.currentTimeMillis();

    bufor_f = 0;
    restart = 0;
    gameRunning = true;

    init_klawiatura();
    i = 0;
    j = 0;
    k = 0;
    while (klawiatura(g, "please enter your login     ", 10) == 1) {
     try {
      Thread.sleep(10);
     } catch (InterruptedException ie) {
      stop();
     }
     switch (j) {
      case 0:
       k = 0 - ((((logo - width)) * i) / 600);
       i++;
       break;
      case 1:
       k = 0 - (logo - width);
       i++;
       break;
      case 2:
       k = 0 - ((((logo - width)) * (600 - i)) / 600);
       i++;
       break;
      case 3:
       k = 0;
       i++;
       break;
     }
     if (i > 599) {
      i = 0;
      j++;
      if (j > 3) j = 0;
     }
     g.drawImage(glowna, k, 0, Graphics.TOP | Graphics.LEFT);
     g.drawImage(gamelogo, 0 - ((480 - width) / 2), 0, Graphics.TOP | Graphics.LEFT);
    }

    k = i;

    message2 = "L";
    if ((szerokosc + 3) > 64)
     message2 = message2 + ((char) 128);
    else
     message2 = message2 + ((char)(szerokosc + 3 + 64));
    if ((wysokosc + 3) > 64)
     message2 = message2 + ((char) 128);
    else
     message2 = message2 + ((char)(wysokosc + 3 + 64));
    //message2=message2+"L";

    for (i = 0; i < 10; i++)
     nick_gracza[i] = 32 + 1;
    for (i = 0; i < key_pozycja; i++) {
     message2 = message2 + ((char) key_input[i]);
     nick_gracza[i] = key_input[i] + 1;
    }

    init_klawiatura();
    i = k;
    k = 0;
    while (klawiatura(g, "please enter your password", 10) == 1) {
     try {
      Thread.sleep(10);
     } catch (InterruptedException ie) {
      stop();
     }
     switch (j) {
      case 0:
       k = 0 - ((((logo - width)) * i) / 600);
       i++;
       break;
      case 1:
       k = 0 - (logo - width);
       i++;
       break;
      case 2:
       k = 0 - ((((logo - width)) * (600 - i)) / 600);
       i++;
       break;
      case 3:
       k = 0;
       i++;
       break;
     }
     if (i > 599) {
      i = 0;
      j++;
      if (j > 3) j = 0;
     }
     g.drawImage(glowna, k, 0, Graphics.TOP | Graphics.LEFT);
     g.drawImage(gamelogo, 0 - ((480 - width) / 2), 0, Graphics.TOP | Graphics.LEFT);
    }
    message2 = message2 + ":";
    for (i = 0; i < key_pozycja; i++) {
     message2 = message2 + ((char) key_input[i]);
    }



    init_klawiatura();
    i = k;
    k = 0;
    while (klawiatura(g, "please enter server address", 20) == 1) {
     try {
      Thread.sleep(10);
     } catch (InterruptedException ie) {
      stop();
     }
     switch (j) {
      case 0:
       k = 0 - ((((logo - width)) * i) / 600);
       i++;
       break;
      case 1:
       k = 0 - (logo - width);
       i++;
       break;
      case 2:
       k = 0 - ((((logo - width)) * (600 - i)) / 600);
       i++;
       break;
      case 3:
       k = 0;
       i++;
       break;
     }
     if (i > 599) {
      i = 0;
      j++;
      if (j > 3) j = 0;
     }
     g.drawImage(glowna, k, 0, Graphics.TOP | Graphics.LEFT);
     g.drawImage(gamelogo, 0 - ((480 - width) / 2), 0, Graphics.TOP | Graphics.LEFT);
    }

    address2 = "";
    for (i = 0; i < key_pozycja; i++) {
     address2 = address2 + ((char) key_input[i]);
    }



    System.out.println("msg " + message2);

    g.drawString("laczenie z serwerem...", 0, 11 * (height / 12), Graphics.TOP | Graphics.LEFT);

    try {
     String connect_address = "socket://185.157.80.235:2123";
     if (address2 != "") connect_address = "socket://" + address2 + ":2123";
     sc = (SocketConnection) Connector.open(connect_address);
     //            		sc = (SocketConnection) Connector.open("socket://84.10.105.176:2123");
     is = sc.openInputStream();
     os = sc.openOutputStream();
    } catch (IOException ioe) {
     ioe.printStackTrace();
     //gameRunning=false;
     g.setColor(255, 255, 255);
     g.drawString("Connection failed!", 0, 0, Graphics.TOP | Graphics.LEFT);
    }

    try {
     os.write(message2.getBytes());
    } catch (IOException ioe) {
     ioe.printStackTrace();
    }



    flushGraphics();
    gracz_hp = 1;
    while (gameRunning) //is true
    {
     if ((kierunek_buf != 0) && ((klatka_sek != 0) || (klatka_sek != 4) || (klatka_sek != 8) || (klatka_sek != 12))) {
      kierunek = 0;
     }
     if ((spell_serw_info == 3) || (spell_serw_info == 2)) {
      cast_loc--;
      for (i = 0; i < (cast_loc); i++) {
       cast_loc_nr[i] = cast_loc_nr[i + 1];
      }
      if (cast_loc > 0) {
       cast_start = System.currentTimeMillis();
       cast_time = procedureclass.casttime(cast_loc_nr[0]);
      }
      spell_serw_info = -1;
      koniec_casta = 2;
     }

     input();
     if (gracz_hp == 0) { //jezeli gracz padl
      if (dead_secs == -1) {
       dead_secs_start = System.currentTimeMillis();
       dead_secs_end = System.currentTimeMillis() + 30999;
       dead_secs = 3;
      }
      if ((System.currentTimeMillis() > dead_secs_end) && (dead_secs == 3)) {
       dead_secs = 2;
      }
      if (dead_secs == 2) {
       if (firea == 1) dead_secs = 1;
      }
      kierunek = 0;
      fire = 0;
      firea = 0;
      fireb = 0;
      firec = 0;
      fired = 0;
      fired_buf = 0;
      cast_loc = 0;
      inventory_pokaz = 0;
      discard_pokaz = 0;
      menu_pokaz = 0;
      char_pokaz = 0;
      quest_pokaz = 0;
     }
     for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
      if (postac_serw[nr_target_loc] == nr_target) break;
     }
     if ((post_hp[nr_target_loc] < 1) && ((postac_serw[nr_target_loc] / 4096) != 1)) {
      nr_target = -1;
     }
     if (inventory_pokaz == 1) { //qwerty
      if (discard_pokaz == 1) {
       if ((kierunek == 1) && (k_odb == 1)) {
        k_odb = 0;
       }
       if ((kierunek == 2) && (k_odb == 1)) {
        discard_x++;
        k_odb = 0;
       }
       if ((kierunek == 3) && (k_odb == 1)) {
        k_odb = 0;
       }
       if ((kierunek == 4) && (k_odb == 1)) {
        discard_x--;
        k_odb = 0;
       }
       if (discard_x < 0) discard_x = 0;
       if (discard_x > 1) discard_x = 1;
       if (fire_buf == 1) {
        if (discard_x == 1) discard_pokaz = 0;
        if (discard_x == 0) {
         if (discard_item == 0) {
          discard_item = 1;
          discard_pokaz = 0;
         }
        }
       }
      } else {
       inventory_cap = 15 + inventory_bagi[0] + inventory_bagi[1] + inventory_bagi[2] + inventory_bagi[3];
       int ilosc_linii_inv2 = inventory_cap / 5;
       int ilosc_w_ostatniej2 = inventory_cap - (ilosc_linii_inv2 * 5);
       if (ilosc_w_ostatniej2 > 0) ilosc_linii_inv2++;
       if ((kierunek == 1) && (k_odb == 1)) {
        menu_y--;
        k_odb = 0;
       }
       if ((kierunek == 2) && (k_odb == 1)) {
        menu_x++;
        k_odb = 0;
       }
       if ((kierunek == 3) && (k_odb == 1)) {
        menu_y++;
        k_odb = 0;
       }
       if ((kierunek == 4) && (k_odb == 1)) {
        menu_x--;
        k_odb = 0;
       }
       if (menu_x > 4) menu_x = 4;
       if (menu_x < 0) menu_x = 0;
       if (menu_y > (ilosc_linii_inv2 - 1)) menu_y = ilosc_linii_inv2 - 1;
       if (menu_y < 0) menu_y = 0;
       if ((menu_y == (ilosc_linii_inv2 - 1)) && (menu_x > (ilosc_w_ostatniej2 - 1)) && (ilosc_w_ostatniej2 > 0)) menu_x = ilosc_w_ostatniej2 - 1;
       if (fire_buf == 1) {
        if (uzyj_item == 0)
         uzyj_item = 1;
       }
      }
      if (fire == 1) fire_buf = 1;
      kierunek = 0;
      fire = 0;
      firea = 0;
      firec = 0;
      cast_loc = 0;
     }
     if (menu_pokaz == 1) {
      if ((kierunek == 2) && (k_odb == 1)) {
       menu_x++;
       k_odb = 0;
      }
      if ((kierunek == 4) && (k_odb == 1)) {
       menu_x--;
       k_odb = 0;
      }
      if (menu_x > 4) menu_x = 4;
      if (menu_x < 0) menu_x = 0;
      if (fire_buf == 1) {
       switch (menu_x) {
        case 0:
         menu_pokaz = 0;
         quest_pokaz = 1;
         menu_x = 0;
         menu_y = 0;
         break;
        case 3:
         menu_pokaz = 0;
         char_pokaz = 1;
         menu_x = 0;
         break;
        case 4:
         menu_pokaz = 0;
         inventory_pokaz = 1;
         menu_x = 0;
         break;
       }
      }
      if (fire == 1) fire_buf = 1;
      kierunek = 0;
      fire = 0;
      firea = 0;
      fireb = 0;
      firec = 0;
      cast_loc = 0;
     }
     if (char_pokaz == 1) {
      if ((kierunek == 1) && (k_odb == 1)) {
       menu_y--;
       k_odb = 0;
      }
      if ((kierunek == 2) && (k_odb == 1)) {
       menu_x++;
       k_odb = 0;
       if (menu_x == 1) menu_x = 4;
      }
      if ((kierunek == 3) && (k_odb == 1)) {
       menu_y++;
       k_odb = 0;
      }
      if ((kierunek == 4) && (k_odb == 1)) {
       menu_x--;
       k_odb = 0;
       if (menu_x == 3) menu_x = 0;
      }
      if (menu_y < 0) menu_y = 0;
      if (menu_y > 6) menu_y = 6;
      if (menu_x > 4) menu_x = 4;
      if (menu_x < 0) menu_x = 0;
      //if(fire_buf==1) {
      //	if(menu_x==3) { menu_pokaz=0; inventory_pokaz=1; menu_x=0; }
      //}
      if (fire == 1) fire_buf = 1;
      kierunek = 0;
      fire = 0;
      firea = 0;
      fireb = 0;
      firec = 0;
      cast_loc = 0;
     }
     if (quest_pokaz == 1) {
      if ((kierunek == 1) && (k_odb == 1)) {
       menu_y--;
       k_odb = 0;
      }
      if ((kierunek == 2) && (k_odb == 1)) {
       menu_x++;
       k_odb = 0;
      }
      if ((kierunek == 3) && (k_odb == 1)) {
       menu_y++;
       k_odb = 0;
       menu_x = 2;
      }
      if ((kierunek == 4) && (k_odb == 1)) {
       menu_x--;
       k_odb = 0;
       if (menu_y != 0) menu_y = 0;
      }
      if (menu_y < 0) menu_y = 0;
      if (menu_y > 2) menu_y = 2;
      if (menu_x > 2) menu_x = 2;
      if (menu_x < 0) menu_x = 0;
      //pozycja_linii_q ilosc_linii_q
      if (fire_buf == 1) {
       if (menu_y == 2) {
        pozycja_linii_q++;
       }
       if (menu_y == 1) {
        pozycja_linii_q--;
       }
       if ((pozycja_linii_q + 14) > ilosc_linii_q) pozycja_linii_q = ilosc_linii_q - 14;
       if (pozycja_linii_q < 0) pozycja_linii_q = 0;
      }
      if (fire == 1) fire_buf = 1;
      kierunek = 0;
      fire = 0;
      firea = 0;
      fireb = 0;
      firec = 0;
      cast_loc = 0;
     }
     if (firea == 1) { //pierwsza karta spelli
      if ((kierunek == 4) && (k_odb == 1) && (procedureclass.odleglosc(post_x[nr_target_loc] - pozycja_x, post_y[nr_target_loc] - pozycja_y, klocki * procedureclass.zasieg_spella(1))) == 1) {
       if (((nr_target / (128 * 32)) == 2) && (post_hp[nr_target_loc] > 0)) {
        switch (cast_loc) {
         case 0:
          cast_loc = 1;
          cast_loc_nr[0] = 1;
          cast_nr[cast] = 1;
          cast++;
          cast_start = System.currentTimeMillis();
          cast_time = procedureclass.casttime(cast_loc_nr[0]);
          spell_serw_info = 4;
          break;
         case 1:
          cast_loc = 2;
          cast_loc_nr[1] = 1;
          cast_nr[cast] = 1;
          cast++;
          break;
        }
       }
       k_odb = 0;
      }
      kierunek = 0;
     }
     if (fireb == 1) {
      if (inventory_pokaz == 0) { //
       if ((kierunek == 3) && (k_odb == 1)) {
        k_odb = 0;
        podnies_loot = 1;
       }
       kierunek = 0;
      }
     }
     if (fireb_buf == 1) {
      if (inventory_pokaz == 1) {
       if (1 == 1) {
        discard_x = 1;
        discard_pokaz++;
        if (discard_pokaz > 1) discard_pokaz = 0;
       }
      }
      fireb_buf = 0;
     }
     if (firec == 1) {
      if ((kierunek == 3) && (k_odb == 1)) { //nie sprawdza odleglosci bo moze byc to potwor
       if ((post_hp[nr_target_loc] > 0) || (nr_target == -1)) { //musi byc zywa, jesli gracz nie ma targeta - leczy siebie
        switch (cast_loc) {
         case 0:
          cast_loc = 1;
          cast_loc_nr[0] = 102;
          cast_nr[cast] = 102;
          cast++;
          cast_start = System.currentTimeMillis();
          cast_time = procedureclass.casttime(cast_loc_nr[0]);
          spell_serw_info = 4;
          break;
         case 1:
          cast_loc = 2;
          cast_loc_nr[1] = 102;
          cast_nr[cast] = 102;
          cast++;
          break;
          //case 2: cast_loc=3; cast_loc_nr[2]=102; cast_nr[cast]=102; cast++; break;
        }
       }
       k_odb = 0;
      }
      kierunek = 0;
     }
     if (fired_buf == 1) {
      if (inventory_pokaz == 0) {
       if ((menu_pokaz == 0) && (char_pokaz == 0) && (quest_pokaz == 0)) {
        menu_pokaz = 1;
        menu_x = 4;
       } else {
        menu_pokaz = 0;
        char_pokaz = 0;
        quest_pokaz = 0;
       }
      } else {
       inventory_pokaz = 0;
       discard_pokaz = 0;
      }
      fired_buf = 0;
      kierunek = 0;
     }
     if (cast_loc < 1) {
      spell_serw_info = -1;
      cast_loc = 0;
     } //zapobiega opoznionym sygnalom (jesli nie castujesz nic to sygnaly z serwera nic nie oznaczaja
     if (spell_serw_info == 4) {
      cast_start = System.currentTimeMillis();
      cast_time = procedureclass.casttime(cast_loc_nr[0]);
     }

     if (spell_serw_info_pozycja > 0) { //przekazywanie informacji dotyczacych spelli z bufora
      spell_serw_info = spell_serw_info_bufor[0];
      spell_serw_info_pozycja--;
      for (i = 0; i < spell_serw_info_pozycja; i++)
       spell_serw_info_bufor[i] = spell_serw_info_bufor[i + 1];
     }
     int spell_serw_info = -1;
     int spell_serw_info_pozycja = 0;
     int[] spell_serw_info_bufor = new int[50];

     if ((kierunek != 0) && (cast_loc > 0)) {
      przerwano_cast = 1;
      cast_loc = 0;
      spell_serw_info = -1;
     }

     if (fire == 1) { //wybieranie targetu
      int wybrano = 0;
      int centrum_x = 0;
      int centrum_y = 0;
      int gracz_x = pozycja_x / klocki;
      int gracz_y = pozycja_y / klocki;
      if ((kierunek == 1) && (k_odb == 1)) {
       if (nr_target == -1) {
        centrum_x = gracz_x;
        centrum_y = gracz_y - 1;
       } else {
        for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
         if (postac_serw[nr_target_loc] == nr_target) break;
        }
        centrum_x = post_x_doc[nr_target_loc] / klocki;
        centrum_y = post_y_doc[nr_target_loc] / klocki;
        if ((centrum_x < (gracz_x - 7)) || (centrum_x > (gracz_x + 7)) || (centrum_y < (gracz_y - 7)) && (centrum_y > (gracz_y + 7))) {
         nr_target = -1;
        }
       }
       for (i = centrum_y; i > (gracz_y - 8); i--) {
        for (k = gracz_x - 7; k < gracz_x + 8; k++) {
         for (j = 1; j < ilosc_postaci; j++) {
          if (((post_y_doc[j] / klocki) == i) && ((post_x_doc[j] / klocki) == k)) {
           if (((wybrano == 1) || (nr_target == -1)) && ((post_hp[j] > 0) || ((postac_serw[j] / 4096) == 1))) {
            nr_target = postac_serw[j];
            wybrano = 2;
           }
           if ((nr_target == postac_serw[j]) && (wybrano == 0)) wybrano = 1;
          }
         }
        }
       }
       if (wybrano == 0) nr_target = -1;
       k_odb = 0;
      }
      if ((kierunek == 2) && (k_odb == 1)) {
       if (nr_target == -1) {
        centrum_x = gracz_x;
        centrum_y = gracz_y - 1;
       } else {
        for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
         if (postac_serw[nr_target_loc] == nr_target) break;
        }
        centrum_x = post_x_doc[nr_target_loc] / klocki;
        centrum_y = post_y_doc[nr_target_loc] / klocki;
        if ((centrum_x < (gracz_x - 7)) || (centrum_x > (gracz_x + 7)) || (centrum_y < (gracz_y - 7)) && (centrum_y > (gracz_y + 7))) {
         nr_target = -1;
        }
       }
       for (k = centrum_x; k < (gracz_x + 8); k++) {
        for (i = (gracz_y - 7); i < (gracz_y + 8); i++) {
         for (j = 1; j < ilosc_postaci; j++) {
          if (((post_y_doc[j] / klocki) == i) && ((post_x_doc[j] / klocki) == k)) {
           if (((wybrano == 1) || (nr_target == -1)) && ((post_hp[j] > 0) || ((postac_serw[j] / 4096) == 1))) {
            nr_target = postac_serw[j];
            wybrano = 2;
           }
           if ((nr_target == postac_serw[j]) && (wybrano == 0)) wybrano = 1;
          }
         }
        }
       }
       if (wybrano == 0) nr_target = -1;
       k_odb = 0;
      }
      if ((kierunek == 3) && (k_odb == 1)) {
       if (nr_target == -1) {
        centrum_x = gracz_x;
        centrum_y = gracz_y - 1;
       } else {
        for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
         if (postac_serw[nr_target_loc] == nr_target) break;
        }
        centrum_x = post_x_doc[nr_target_loc] / klocki;
        centrum_y = post_y_doc[nr_target_loc] / klocki;
        if ((centrum_x < (gracz_x - 7)) || (centrum_x > (gracz_x + 7)) || (centrum_y < (gracz_y - 7)) && (centrum_y > (gracz_y + 7))) {
         //nr_target=-1;
        }
       }
       for (i = centrum_y; i < (gracz_y + 8); i++) {
        for (k = gracz_x - 7; k < gracz_x + 8; k++) {
         for (j = 1; j < ilosc_postaci; j++) {
          if (((post_y_doc[j] / klocki) == i) && ((post_x_doc[j] / klocki) == k)) {
           if (((wybrano == 1) || (nr_target == -1)) && ((post_hp[j] > 0) || ((postac_serw[j] / 4096) == 1))) {
            nr_target = postac_serw[j];
            wybrano = 2;
           }
           if ((nr_target == postac_serw[j]) && (wybrano == 0)) wybrano = 1;
          }
         }
        }
       }
       if (wybrano == 0) nr_target = -1;
       k_odb = 0;
      }
      if ((kierunek == 4) && (k_odb == 1)) {
       if (nr_target == -1) {
        centrum_x = gracz_x;
        centrum_y = gracz_y - 1;
       } else {
        for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
         if (postac_serw[nr_target_loc] == nr_target) break;
        }
        centrum_x = post_x_doc[nr_target_loc] / klocki;
        centrum_y = post_y_doc[nr_target_loc] / klocki;
        if ((centrum_x < (gracz_x - 7)) || (centrum_x > (gracz_x + 7)) || (centrum_y < (gracz_y - 7)) && (centrum_y > (gracz_y + 7))) {
         nr_target = -1;
        }
       }
       for (k = centrum_x; k > (gracz_x - 8); k--) {
        for (i = (gracz_y - 7); i < (gracz_y + 8); i++) {
         for (j = 1; j < ilosc_postaci; j++) {
          if (((post_y_doc[j] / klocki) == i) && ((post_x_doc[j] / klocki) == k)) {
           if (((wybrano == 1) || (nr_target == -1)) && ((post_hp[j] > 0) || ((postac_serw[j] / 4096) == 1))) {
            nr_target = postac_serw[j];
            wybrano = 2;
           }
           if ((nr_target == postac_serw[j]) && (wybrano == 0)) wybrano = 1;
          }
         }
        }
       }
       if (wybrano == 0) nr_target = -1;
       k_odb = 0;
      }
      kierunek = 0;
     }
     if (fire == 1) kierunek = 0;
     if ((fire_buf == 1) && ((inventory_pokaz == 1) || (menu_pokaz == 1) || (char_pokaz == 1) || (quest_pokaz == 1))) {
      fire = 1;
      fire_buf = 0;
     }
     if (((klatka_sek == 0) || (klatka_sek == 4) || (klatka_sek == 8) || (klatka_sek == 12)) && (zalogowany == 1)) {
      cykl_chmury += (width / 64);
      if (cykl_chmury > width) {
       cykl_chmury = 0 - width;
      }
      if ((((pozycja_x / klocki) * klocki) == pozycja_x) && (((pozycja_y / klocki) * klocki) == pozycja_y)) {
       kierunek_pamiec = 0;
       int dodatkowa_klatka = koniec_casta / 2; //(cast_loc+7)/8; //jesli castuje to dodatkowa klatka to 1 jesli nie to 0
       koniec_casta = 1;
       switch (kierunek) {
        case 0:
         kierunek_pamiec = kierunek;
         glownychar = (((glownychar - 1) / 12) * 12) + 2 + (dodatkowa_klatka * ((klatka_anim + 1) / 2));
         break;
        case 1:
         glownychar = 36 + 14;
         if (kolizje((pozycja_x / klocki), (pozycja_y / klocki) - 1) == 0) {
          kierunek_pamiec = kierunek;
         }
         break;
        case 2:
         glownychar = 24 + 14;
         if (kolizje((pozycja_x / klocki) + 1, (pozycja_y / klocki)) == 0) {
          kierunek_pamiec = kierunek;
         }
         break;
        case 3:
         glownychar = 00 + 14;
         if (kolizje((pozycja_x / klocki), (pozycja_y / klocki) + 1) == 0) {
          kierunek_pamiec = kierunek;
         }
         break;
        case 4:
         glownychar = 12 + 14;
         if (kolizje((pozycja_x / klocki) - 1, (pozycja_y / klocki)) == 0) {
          kierunek_pamiec = kierunek;
         }
         break;
       }
       if (gracz_hp == 0) glownychar = 1;
       //					kierunek_pamiec=kierunek;
      }
      kierunek_buf = kierunek;
      if (exp_wyswietl > 0) exp_wyswietl--;
      if (lvl_up > 0) lvl_up--;
      if (odebrano_loot > 0) {
       odebrano_loot_czas--;
       if (odebrano_loot_czas < 1) {
        odebrano_loot = 0;
        odebrano_loot_czas = 0;
       }
      }

      //miejsce gdzie moga wyliczac sie rozne operacje

      for (i = 0; i < ilosc_postaci; i++) {
       dmg_arr[i] -= 4;
       if (dmg_arr[i] < 0) dmg_arr[i] = 0;
      }

      klatka_anim++; //animacja wody wartosc 0-2
      if (klatka_anim > 2) {
       klatka_anim = 0;
      }

      if (kierunek_pamiec == 1) {
       pozycja_y = pozycja_y - 4;
       glownychar = 36 + 14;
       if ((pozycja_y - ((pozycja_y / klocki) * klocki)) < 5) {
        glownychar--;
       }
       if ((pozycja_y - ((pozycja_y / klocki) * klocki)) > 10) {
        glownychar++;
       }
      }
      if (kierunek_pamiec == 2) {
       pozycja_x = pozycja_x + 4;
       glownychar = 24 + 14;
       if ((pozycja_x - ((pozycja_x / klocki) * klocki)) < 5) {
        glownychar--;
       }
       if ((pozycja_x - ((pozycja_x / klocki) * klocki)) > 10) {
        glownychar++;
       }
      }
      if (kierunek_pamiec == 3) {
       pozycja_y = pozycja_y + 4;
       glownychar = 00 + 14;
       if ((pozycja_y - ((pozycja_y / klocki) * klocki)) < 5) {
        glownychar--;
       }
       if ((pozycja_y - ((pozycja_y / klocki) * klocki)) > 10) {
        glownychar++;
       }
      }
      if (kierunek_pamiec == 4) {
       pozycja_x = pozycja_x - 4;
       glownychar = 12 + 14;
       if ((pozycja_x - ((pozycja_x / klocki) * klocki)) < 5) {
        glownychar--;
       }
       if ((pozycja_x - ((pozycja_x / klocki) * klocki)) > 10) {
        glownychar++;
       }
      }

      kamera_x = pozycja_x - ((width - 4) / 2);
      kamera_y = pozycja_y - ((height - 25 - 4) / 2); //25 to wysokosc paska na dole ekranu

      if (kamera_x > ((plansza_x * klocki) - width)) {
       kamera_x = (plansza_x * klocki) - width;
      }
      if (kamera_x < 0) {
       kamera_x = 0;
      }
      if (kamera_y > ((plansza_y * klocki) - height + 25)) {
       kamera_y = (plansza_y * klocki) - height + 25;
      }
      if (kamera_y < 0) {
       kamera_y = 0;
      }

      for (i = 1; i < ilosc_postaci; i++) {
       for (j = 0; j < 1; j++) {
        if (post_x[i] > post_x_doc[i]) {
         post_x[i] -= postac_mode[i];
         post_kierunek[i] = 4;
         break;
        }
        if (post_x[i] < post_x_doc[i]) {
         post_x[i] += postac_mode[i];
         post_kierunek[i] = 2;
         break;
        }
        if (post_y[i] > post_y_doc[i]) {
         post_y[i] -= postac_mode[i];
         post_kierunek[i] = 1;
         break;
        }
        if (post_y[i] < post_y_doc[i]) {
         post_y[i] += postac_mode[i];
         post_kierunek[i] = 3;
         break;
        }
       }
      }

      if (nr_target > -1) { // sprawdzenie czy target nie oddalil sie o 16 kratek (zabiera go z targeta)
       for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
        if (postac_serw[nr_target_loc] == nr_target) break;
       }
       int r_poz = post_x_doc[nr_target_loc] - pozycja_x;
       if ((r_poz < -(klocki * 12)) || (r_poz > (klocki * 8))) nr_target = -1;
       r_poz = post_y_doc[nr_target_loc] - pozycja_y;
       if ((r_poz < -(klocki * 12)) || (r_poz > (klocki * 8))) nr_target = -1;
      }
      commclass.comm();
     }
     if (zalogowany == 0) {
      commclass.comm();
      if (version == -1) {
       g.setColor(255, 0, 0);
       g.drawString("VERSION MISMATCH!", 0, 40, Graphics.TOP | Graphics.LEFT);
       flushGraphics();
       while (1 == 1) {
        try {
         Thread.sleep(100);
        } catch (InterruptedException ie) {
         stop();
        }
       }
      }
     }



     long endTime = System.currentTimeMillis();
     long duration = (int)(endTime - startTime);
     gameDuration = duration / 1000; //game time in seconds

     czas3 = System.currentTimeMillis();
     klatka++;
     if ((((int)(czas3 - czas2)) + l2 > timeStep2) && (frameskip < 3)) {
      frameskip++;
      timeStep2 = timeStep2 + 40;
     }


     if (klatka > frameskip)
      klatka = 0;
     klatka_sek++;
     if (klatka_sek >= 15)
      klatka_sek = 0;

     if (restart > 0) {
      restart--;
      if (restart == 0) {
       gameRunning = false;
       zycia--;
       if (zycia < 0) {
        zycia = 0;
        Gra = false;
       }
      }
     }


     czas3 = System.currentTimeMillis();
     if (klatka == 0) {
      gr = 1;
      render(g);
      czas1 = System.currentTimeMillis();
      if (czas2 != 0) {
       l2 = 0;
       tab_klatki[nr_klatki] = (int)(czas1 - czas3);
       for (i = 0; i < 8; i++) {
        if (tab_klatki[i] > l2)
         l2 = tab_klatki[i]; //czas maksymalnego renderu w ciagu 8 ostatnich klatek
       }
       nr_klatki++;
       if (nr_klatki > 7)
        nr_klatki = 0;

       czas3 = System.currentTimeMillis();
       l = (int)(czas3 - czas2);
       if (l == 0)
        l = 16;
       try {
        timeStep = (timeStep2 - l);
        if (timeStep < 5)
         timeStep = 5;

        Thread.sleep(timeStep); //- duration);
       } catch (InterruptedException ie) {
        stop();
       }


       //l=l-timeStep;
       // mozna dac jeszcze wiekszy frameskip ale sie pomysli potem. zobaczymy jak bedzie dzialac jak wszystko dodamy
       if ((l) < 250) {
        frameskip = 4;
        timeStep2 = 250;
       }
       if ((l) < 200) {
        frameskip = 3;
        timeStep2 = 200;
       }
       if ((l) < 150) {
        frameskip = 2;
        timeStep2 = 150;
       }
       if ((l) < 100) {
        frameskip = 1;
        timeStep2 = 100;
       }
       if (l < 50) {
        frameskip = 0;
        timeStep2 = 50;
       }
       if ((l) >= 350) {
        frameskip = 7;
        if (l > 390) {
         timeStep2 = 10;
        } else {
         timeStep2 = 400;
        }
       }
       frameskip = 0;
       timeStep2 = 40;
       //timeStep=timeStep2;
       czas1 = System.currentTimeMillis();
      }
      czas2 = czas1;
     } //koniec if(klatka==0)
     else {
      try {
       i = 2;
       Thread.sleep(i); //- duration);
      } catch (InterruptedException ie) {
       stop();
      }
     }

    }
    if (0 > 1)
     Gra = false;
   }
  }
 }



 private void input() {
  int keyStates2 = getKeyStates();
  k = 0;
  if (((keyStates & LEFT_PRESSED) != 0) || ((keyStates2 & LEFT_PRESSED) != 0)) {
   if (kierunek_c == 0) k_odb = 1;
   kierunek = 4;
   kierunek_c = 4;
   k = 1;
  }
  if (((keyStates & RIGHT_PRESSED) != 0) || ((keyStates2 & RIGHT_PRESSED) != 0)) {
   if (kierunek_c == 0) k_odb = 1;
   kierunek = 2;
   kierunek_c = 2;
   k = 1;
  }
  if (((keyStates & UP_PRESSED) != 0) || ((keyStates2 & UP_PRESSED) != 0)) {
   if (kierunek_c == 0) k_odb = 1;
   kierunek = 1;
   kierunek_c = 1;
   k = 1;
  }
  if (((keyStates & DOWN_PRESSED) != 0) || ((keyStates2 & DOWN_PRESSED) != 0)) {
   if (kierunek_c == 0) k_odb = 1;
   kierunek = 3;
   kierunek_c = 3;
   k = 1;
  }
  if (k == 0) {
   k_odb = 0;
   kierunek_c = 0;
  }
  if (((keyStates & FIRE_PRESSED) != 0) || ((keyStates2 & FIRE_PRESSED) != 0)) {
   if (fire == 0) fire_buf = 1;
   fire = 1;
  } else {
   fire = 0;
   fire_buf = 0;
  }
  if (((keyStates & GAME_A_PRESSED) != 0) || ((keyStates2 & GAME_A_PRESSED) != 0)) {
   if (firea == 0) firea_buf = 1;
   firea = 1;
  } else {
   firea = 0;
   firea_buf = 0;
  }
  if (((keyStates & GAME_B_PRESSED) != 0) || ((keyStates2 & GAME_B_PRESSED) != 0)) {
   if (fireb == 0) fireb_buf = 1;
   fireb = 1;
  } else {
   fireb = 0;
   fireb_buf = 0;
  }
  if (((keyStates & GAME_C_PRESSED) != 0) || ((keyStates2 & GAME_C_PRESSED) != 0)) {
   if (firec == 0) firec_buf = 1;
   firec = 1;
  } else {
   firec = 0;
   firec_buf = 0;
  }
  if (((keyStates & GAME_D_PRESSED) != 0) || ((keyStates2 & GAME_D_PRESSED) != 0)) {
   if (fired == 0) fired_buf = 1;
   fired = 1;
  } else {
   fired = 0;
   fired_buf = 0;
  }
 }

 private void render(Graphics g) {
  if (zalogowany == 0) {
   g.setColor(0, 0, 0);
   g.fillRect(0, 0, getWidth(), getHeight());
   g.setColor(120, 120, 120);
   g.drawString("Logging in...", 1, 1, Graphics.TOP | Graphics.LEFT);
   g.setColor(255, 255, 255);
   g.drawString("Logging in...", 0, 0, Graphics.TOP | Graphics.LEFT);
   flushGraphics();
  }
  if ((gr == 1) && (zalogowany == 1)) {
   long czas_renderu = System.currentTimeMillis();
   int c = 0;
   int przesun_x = 0;
   int przesun_y = 0;
   if (width > (plansza_x * klocki))
    przesun_x = (width - (plansza_x * klocki)) / 2;
   if (height > (plansza_y * klocki))
    przesun_y = (height - (plansza_y * klocki)) / 2;

   if (kapsula_o == 1) {
    g.setColor(255, 255, 255); // white

   } else {
    g.setColor(0, 0, 0); //
   }
   g.fillRect(0, 0, getWidth(), getHeight());

   postac_g.setCell(0, 0, glownychar); //glowny char

   pasek_en.setCell(1, 0, 1);

   for (int numer_litery = 0; numer_litery < 10; numer_litery++) //skopiuj napis z logina
    font_m.setCell(numer_litery, 0, nick_gracza[numer_litery]);

   if (nr_target > -1) { // wyswietlanie panelu targeta
    for (nr_target_loc = 1; nr_target_loc < ilosc_postaci; nr_target_loc++) {
     if (postac_serw[nr_target_loc] == nr_target) break;
    }
    pasek_en.setCell(0, 0, 1);
    i = 0;
    for (int numer_litery = 0; numer_litery < 10; numer_litery++) {
     font_t.setCell(numer_litery, 0, 32);
    }
    for (int numer_litery = 0; numer_litery < 10; numer_litery++) {
     if (napisy_arr[(nr_target_loc * 10) + numer_litery] != 32) {
      font_t.setCell(i, 0, napisy_arr[(nr_target_loc * 10) + numer_litery]);
      i++;
     }
    }
    if ((postac_serw[nr_target_loc] / 4096) > 0) { //pokazuj te dane tylko jesli to nie jest vendor
     if (post_lvl[nr_target_loc] > 9) {
      numerki_lvl[0].setCell(0, 0, 49 + (post_lvl[nr_target_loc] / 10));
      numerki_lvl[0].setPosition(width - (54 * 2) + 2, 10 + 6);
     } else {
      numerki_lvl[0].setCell(0, 0, 0);
      numerki_lvl[0].setPosition(width - (54 * 2) - 1, 10 + 6);
     }
     numerki_lvl[0].setCell(1, 0, 49 + post_lvl[nr_target_loc] - (post_lvl[nr_target_loc] / 10) * 10);
    } else { //jesli to nie vendor wyzeruj numerki
     numerki_lvl[0].setCell(0, 0, 32);
     numerki_lvl[0].setCell(1, 0, 32);
    }
   } else {
    pasek_en.setCell(0, 0, 0);
   }


   for (i = 0; i < (wysokosc) + 3; i++) {
    for (j = 0; j < (szerokosc) + 3; j++) {
     if ((i + (kamera_y / klocki) >= plansza_y) || (j + (kamera_x / klocki)) >= plansza_x || (i + (kamera_y / klocki) < 0) || (j + (kamera_x / klocki)) < 0) {
      canvas01_b.setCell(j, i, 0);
      postacie_b.setCell(j, i, 0);
     } else {
      canvas01_b.setCell(j, i, plansza[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
      postacie_b.setCell(j, i, 0);
      //					canvas02_b.setCell(j, i, plansza_l2[(i+(kamera_y/klocki))*plansza_x+j+(kamera_x/klocki)]);
      switch (klatka_anim) {
       case 0:
        canvas02_b.setCell(j, i, plansza_l2[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        canvas03_b.setCell(j, i, plansza_l3[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        canvas04_b.setCell(j, i, plansza_l4[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        break;

       case 1:
        canvas02_b.setCell(j, i, plansza_l2[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        canvas03_b.setCell(j, i, plansza_l3[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        canvas04_b.setCell(j, i, plansza_l4[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        break;

       case 2:
        canvas02_b.setCell(j, i, plansza_l2[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        canvas03_b.setCell(j, i, plansza_l3[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        canvas04_b.setCell(j, i, plansza_l4[(i + (kamera_y / klocki)) * plansza_x + j + (kamera_x / klocki)]);
        break;
      }
     }
    }
   }


   canvas01_b.setPosition(przesun_x - kamera_x + (kamera_x / klocki) * klocki, 0 - kamera_y + (kamera_y / klocki) * klocki);
   canvas02_b.setPosition(przesun_x - kamera_x + (kamera_x / klocki) * klocki, 0 - kamera_y + (kamera_y / klocki) * klocki);
   canvas03_b.setPosition(przesun_x - kamera_x + (kamera_x / klocki) * klocki, 0 - kamera_y + (kamera_y / klocki) * klocki);
   canvas04_b.setPosition(przesun_x - kamera_x + (kamera_x / klocki) * klocki, 0 - kamera_y + (kamera_y / klocki) * klocki);
   postacie_b.setPosition(przesun_x - kamera_x + (kamera_x / klocki) * klocki, 0 - 16 - kamera_y + (kamera_y / klocki) * klocki);
   postac_g.setPosition(przesun_x + pozycja_x - kamera_x, pozycja_y - kamera_y - 16);


   pasek_en.setPosition(width - (54 * 2) - 2, 2 + 10);

   if (gracz_lvl > 9) {
    numerki_lvl[1].setCell(0, 0, 49 + (gracz_lvl / 10));
    numerki_lvl[1].setPosition(width - 56 + 4, 10 + 6);
   } else {
    numerki_lvl[1].setCell(0, 0, 0);
    numerki_lvl[1].setPosition(width - 56 + 1, 10 + 6);
   }
   numerki_lvl[1].setCell(1, 0, 49 + gracz_lvl - (gracz_lvl / 10) * 10);
   font_m.setPosition(width - 54 + 10, 8 + 10);
   font_t.setPosition(width - (54 * 2) + 10, 8 + 10);



   canvas01_b.paint(g);
   canvas02_b.paint(g);

   for (i = 1; i < ilosc_postaci; i++) {
    j = (((post_typ[i] - 1) / 4));
    switch (post_kierunek[i]) {
     case 0:
      postac[i].setCell(0, 0, 00 + 13 + (j * 12 * 6) + ((post_typ[i] - 1 - (4 * j)) * 3));
      break;
     case 1:
      k = 2 - ((post_y[i] - (((post_y[i]) / 12) * 12)) / 4);
      postac[i].setCell(0, 0, 36 + 13 + (j * 12 * 6) + ((post_typ[i] - 1 - (4 * j)) * 3) + k);
      break;
     case 2:
      k = ((post_x[i] - (((post_x[i]) / 12) * 12)) / 4);
      postac[i].setCell(0, 0, 24 + 13 + (j * 12 * 6) + ((post_typ[i] - 1 - (4 * j)) * 3) + k);
      break;
     case 3:
      k = ((post_y[i] - (((post_y[i]) / 12) * 12)) / 4);
      postac[i].setCell(0, 0, 00 + 13 + (j * 12 * 6) + ((post_typ[i] - 1 - (4 * j)) * 3) + k);
      break;
     case 4:
      k = 2 - ((post_x[i] - (((post_x[i]) / 12) * 12)) / 4);
      postac[i].setCell(0, 0, 12 + 13 + (j * 12 * 6) + ((post_typ[i] - 1 - (4 * j)) * 3) + k);
      break;

    }
    if ((postac_serw[i] / 4096) > 0) {
     if (post_hp[i] == 0)
      postac[i].setCell(0, 0, 00 + 13 + (j * 12 * 6) - 12);
    }
    postac[i].setPosition(przesun_x + post_x[i] - kamera_x, post_y[i] - kamera_y - 16);
    if ((postac_serw[i] / 4096) > 1) {
     napisy[i].setPosition(przesun_x + post_x[i] - kamera_x - 14, post_y[i] - kamera_y - 9 - 8);
     napisy_cien[i].setPosition(przesun_x + post_x[i] - kamera_x - 14 + 1, post_y[i] - kamera_y - 9 + 1 - 8);
    } else {
     napisy[i].setPosition(przesun_x + post_x[i] - kamera_x - 14, post_y[i] - kamera_y - 7 - 8);
     napisy_cien[i].setPosition(przesun_x + post_x[i] - kamera_x - 14 + 1, post_y[i] - kamera_y - 7 + 1 - 8);
    }

    if (((post_x[i] - kamera_x) > (0 - 1 - klocki)) && ((post_x[i] - kamera_x) < (width + 1)) && ((post_y[i] - kamera_y) > (0 - 1 - klocki)) && ((post_y[i] - kamera_y) < (height + 1))) {
     postac[i].paint(g);
     //				napisy[i].paint(g);
     if (postac_serw[i] == nr_target) {
      kursor_b.setPosition(przesun_x + post_x[i] - kamera_x, post_y[i] - kamera_y);
      //kursor_b.setPosition(0,0);
      kursor_b.setCell(0, 0, 1 + (klatka_anim / 2)); //(klatka_anim/2)
      kursor_b.paint(g);
     }
    }
   }
   kursor_menu.setCell(0, 0, 1 + (klatka_anim / 2)); //klatka animacji kursora w menu



   postac_g.paint(g);

   canvas03_b.paint(g);
   canvas04_b.paint(g);



   if (dmg_arr[0] > 0) { //wyswietlanie DMG na naszym graczu
    dmg[0].setPosition(przesun_x + pozycja_x - kamera_x - 3, pozycja_y - kamera_y - 14 + dmg_arr[0]);
    dmg[0].paint(g);
   }
   for (i = 1; i < ilosc_postaci; i++) {
    if (((post_x[i] - kamera_x) > (0 - 1 - klocki)) && ((post_x[i] - kamera_x) < (width + 1)) && ((post_y[i] - kamera_y) > (0 - 1 - klocki)) && ((post_y[i] - kamera_y) < (height + 1))) {
     napisy_cien[i].paint(g);
     napisy[i].paint(g);
     if ((postac_serw[i] / 4096) == 2) { //wyswietlanie ENERGII i MANY ==1 to gracze ==2 to monstery
      if (post_hp[i] > 0) {
       g.setColor(120, 0, 0);
       g.fillRect(przesun_x + post_x[i] - kamera_x - 6, post_y[i] - kamera_y - 3 - 8, 24, 2);
       g.setColor(240, 0, 0);
       g.fillRect(przesun_x + post_x[i] - kamera_x - 6, post_y[i] - kamera_y - 3 - 8, ((post_hp[i] * 24) / post_max_hp[i]), 2);
      }
     }
     if (dmg_arr[i] > 0) { //wyswietlanie DMG
      dmg[i].setPosition(przesun_x + post_x[i] - kamera_x - 3, post_y[i] - kamera_y - 14 + dmg_arr[i]);
      dmg[i].paint(g);
     }
    }
   }


   if (firea == 1) {
    spele_pokaz.setCell(0, 0, 0);
    spele_pokaz.setCell(1, 0, 197);
    spele_pokaz.setCell(2, 0, 0);
    spele_pokaz.setCell(0, 1, 1);
    spele_pokaz.setCell(1, 1, 198 + klatka_anim);
    spele_pokaz.setCell(2, 1, 197);
    spele_pokaz.setCell(0, 2, 0);
    spele_pokaz.setCell(1, 2, 197);
    spele_pokaz.setCell(2, 2, 0);
    spele_pokaz.setPosition(width - 1 - (3 * 12), height - 26 - (3 * 12));
    spele_pokaz.paint(g);
   }
   if (fireb == 1) {
    spele_pokaz.setCell(0, 0, 0);
    spele_pokaz.setCell(1, 0, 197);
    spele_pokaz.setCell(2, 0, 0);
    spele_pokaz.setCell(0, 1, 197);
    spele_pokaz.setCell(1, 1, 198 + klatka_anim);
    spele_pokaz.setCell(2, 1, 197);
    spele_pokaz.setCell(0, 2, 0);
    spele_pokaz.setCell(1, 2, 196);
    spele_pokaz.setCell(2, 2, 0);
    spele_pokaz.setPosition(width - 1 - (3 * 12), height - 26 - (3 * 12));
    spele_pokaz.paint(g);
   }
   if (firec == 1) {
    spele_pokaz.setCell(0, 0, 0);
    spele_pokaz.setCell(1, 0, 11);
    spele_pokaz.setCell(2, 0, 0);
    spele_pokaz.setCell(0, 1, 197);
    spele_pokaz.setCell(1, 1, 198 + klatka_anim);
    spele_pokaz.setCell(2, 1, 14);
    spele_pokaz.setCell(0, 2, 0);
    spele_pokaz.setCell(1, 2, 12);
    spele_pokaz.setCell(2, 2, 0);
    spele_pokaz.setPosition(width - 1 - (3 * 12), height - 26 - (3 * 12));
    spele_pokaz.paint(g);
   }

   long obecny_czas = System.currentTimeMillis();
   int dziesiatki = 0;
   int jednosci = 0;

   switch (cast_loc) {
    case 1:
     spele_b.setCell(0, 0, cast_loc_nr[0]);
     spele_b.setCell(1, 0, 0);
     spele_b.setCell(2, 0, 197);
     spele_b.setPosition((width / 4) - 26, height - 26 - (12) + 2);
     spele_b.paint(g);
     dziesiatki = ((int)(cast_time - obecny_czas + cast_start)) / 1000;
     jednosci = (((int)(cast_time - obecny_czas + cast_start)) / 100) - (dziesiatki * 10);
     if (dziesiatki < 0) dziesiatki = 0;
     if (jednosci < 0) jednosci = 0;
     spele_czas.setCell(0, 0, 49 + dziesiatki);
     spele_czas.setCell(1, 0, 46 + 1);
     spele_czas.setCell(2, 0, 49 + jednosci);
     spele_czas.setPosition((width / 4) - 26 + 12, height - 26 - (12) + 6);
     spele_b.paint(g);
     spele_czas.paint(g);
     break;
    case 2:
     spele_b.setCell(0, 0, cast_loc_nr[0]);
     spele_b.setCell(1, 0, 0);
     spele_b.setCell(2, 0, cast_loc_nr[1]);
     spele_b.setPosition((width / 4) - 26, height - 26 - (12) + 2);
     spele_b.paint(g);
     dziesiatki = ((int)(cast_time - obecny_czas + cast_start)) / 1000;
     jednosci = (((int)(cast_time - obecny_czas + cast_start)) / 100) - (dziesiatki * 10);
     if (dziesiatki < 0) dziesiatki = 0;
     if (jednosci < 0) jednosci = 0;
     spele_czas.setCell(0, 0, 49 + dziesiatki);
     spele_czas.setCell(1, 0, 46 + 1);
     spele_czas.setCell(2, 0, 49 + jednosci);
     spele_czas.setPosition((width / 4) - 26 + 12, height - 26 - (12) + 6);
     spele_b.paint(g);
     spele_czas.paint(g);
     break;
    case 3:
     spele_b.setCell(0, 0, cast_loc_nr[0]);
     spele_b.setCell(1, 0, cast_loc_nr[1]);
     spele_b.setCell(2, 0, cast_loc_nr[2]);
     spele_b.setPosition((width / 4) - 26, height - 26 - (12));
     spele_b.paint(g);
     break;
   }
   if (cast_loc > 0) {
    g.setColor(0, 12, 120);
    g.fillRect((width / 4) - 1 + 12, height - 32, (width / 2) + 2, 3);
    g.fillRect((width / 4) + 12, height - 32 - 1, (width / 2), 3 + 2);
    g.setColor(0, 0, 0);
    g.fillRect((width / 4) + 12, height - 32, (width / 2), 3);
    //			g.fillRect(1, height-26-7, 12, 2);
    g.setColor(0, 48, 180);
    if (obecny_czas > (cast_start + cast_time)) {
     g.fillRect((width / 4) + 12, height - 32, width / 2, 3);
    } else {
     g.fillRect((width / 4) + 12, height - 32, (((width / 2) * ((int)((10 * (obecny_czas - cast_start) / cast_time)))) / 10), 3);
     //				g.fillRect(1, height-26-7, ((12*((int) ((10*(obecny_czas-cast_start)/cast_time))))/10), 2);
    }
   }

   g.setColor(0, 0, 0);
   g.fillRect(width - 49, 29 - 4, 45, 5);
   g.fillRect(width - 49, 35 - 4, 45, 5);
   g.setColor(120, 0, 0);
   g.fillRect(width - 50, 28 - 4, 45, 5);
   g.setColor(240, 0, 0);
   i = 0;
   if (gracz_hp_max > 0) i = ((45 * gracz_hp) / gracz_hp_max);
   g.fillRect(width - 50, 28 - 4, i, 5);
   g.setColor(0, 32, 120);
   g.fillRect(width - 50, 34 - 4, 45, 5);
   g.setColor(0, 64, 240);
   i = 0;
   if (gracz_mp_max > 0) i = ((45 * gracz_mp) / gracz_mp_max);
   g.fillRect(width - 50, 34 - 4, i, 5);

   if (nr_target > -1) { // pasek energii/MP targeta
    if ((postac_serw[nr_target_loc] / 4096) > 0) { //pokazuj te dane tylko jesli to nie jest vendor
     g.setColor(0, 0, 0);
     g.fillRect(width - 49 - 54, 29 - 4, 45, 5);
     g.setColor(120, 0, 0);
     g.fillRect(width - 50 - 54, 28 - 4, 45, 5);
     g.setColor(240, 0, 0);
     i = 0;
     if (post_max_hp[nr_target_loc] > 0) i = ((45 * post_hp[nr_target_loc]) / post_max_hp[nr_target_loc]);
     g.fillRect(width - 50 - 54, 28 - 4, i, 5);
     set_hp(post_hp[nr_target_loc], post_max_hp[nr_target_loc], 0, 0);
     if (post_max_mp[nr_target_loc] > 0) {
      g.setColor(0, 0, 0);
      g.fillRect(width - 49 - 54, 35 - 4, 45, 5);
      g.setColor(0, 32, 120);
      g.fillRect(width - 50 - 54, 34 - 4, 45, 5);
      g.setColor(0, 64, 240);
      i = 0;
      if (post_max_mp[nr_target_loc] > 0) i = ((45 * post_mp[nr_target_loc]) / post_max_mp[nr_target_loc]);
      g.fillRect(width - 50 - 54, 34 - 4, i, 5);
      set_hp(post_mp[nr_target_loc], post_max_mp[nr_target_loc], 0, 1);
     } else {
      for (i = 0; i < 11; i++)
       hp_mp[0].setCell(i, 1, 32);
     }
     hp_mp[0].setPosition(width - 48 - 54, 28 - 4);
     hp_mp[0].paint(g);
    }
   }


   pasek_en.paint(g);
   font_m.paint(g);
   if (nr_target > -1) {
    font_t.paint(g);
    numerki_lvl[0].paint(g);
   }
   numerki_lvl[1].paint(g);
   set_hp(gracz_hp, gracz_hp_max, 1, 0);
   set_hp(gracz_mp, gracz_mp_max, 1, 1);
   hp_mp[1].setPosition(width - 48, 28 - 4);
   hp_mp[1].paint(g);


   g.drawImage(pasek_postac_1, 0 - 1, (height) - 25 - 24 + 5, Graphics.TOP | Graphics.LEFT);
   g.drawImage(pasek_postac_2, width - 12 + 1, (height) - 25 - 24 + 5, Graphics.TOP | Graphics.LEFT);

   i = 0;
   g.drawImage(pasek_dol, i, (height) - 25, Graphics.TOP | Graphics.LEFT);
   for (i = 240; i < width; i += 240)
    g.drawImage(pasek_dol2, i, (height) - 25, Graphics.TOP | Graphics.LEFT);

   //pasek exp
   g.setColor(0, 32, 132);
   i = 0;
   if (gracz_lvl > 0) i = ((width * gracz_exp) / procedureclass.exp(gracz_lvl));
   g.fillRect(0, height - 23, i, 2);

   //ilosc exp w liczbach
   if (gracz_lvl > 0)
    set_hp(gracz_exp, procedureclass.exp(gracz_lvl), 6, 0);
   hp_mp[6].setPosition((width / 2) - 14, height - 24);
   hp_mp[6].paint(g);


   for (i = 0; i < 16; i++) {
    if (i < 8)
     buffy_b.setCell(i, 0, 0);
    if ((i >= 8) && (i < 16))
     buffy_b.setCell(i - 8, 1, 0);
   }

   if (buff_index > 0) {
    //buff_napis.12x4
    if (buff_index > 8) {
     buffy_b = new TiledLayer(8, 2, buffy_sm, 6, 6);
     for (i = 0; i < buff_index; i++) {
      if (i < 8)
       buffy_b.setCell(i, 0, buff[i]);
      if ((i >= 8) && (i < 16))
       buffy_b.setCell(i - 8, 1, buff[i]);
     }
     buffy_b.setPosition(width - 48 - 3, 28 + 12 - 3);
     buffy_b.paint(g);
    } else {
     buffy_b = new TiledLayer(8, 2, buffy, 12, 12);
     for (i = 0; i < buff_index; i++) {
      if (i < 4) {
       buffy_b.setCell(i, 0, buff[i]);
      }
      if ((i >= 4) && (i < 8))
       buffy_b.setCell(i - 4, 1, buff[i]);
     }
     buffy_b.setPosition(width - 48 - 3, 28 + 12 - 3);
     buffy_b.paint(g);
     for (i = 0; i < buff_index; i++) {
      for (j = 0; j < 3; j++)
       buff_napis.setCell(j, 0, 0);
      int buff_czas = ((int)(buff_time[i] - System.currentTimeMillis())) / 1000;
      if (buff_czas < 0) buff_czas = 0;
      if (buff_czas > 59) {
       buff_napis = new TiledLayer(3, 1, fontblue, 4, 6);
       buff_czas = buff_czas / 60;
       buff_napis.setCell(2, 0, 78);
      } else {
       buff_napis = new TiledLayer(3, 1, fontred, 4, 6);
      }
      if (i < 4) {
       if (buff_czas > 9) buff_napis.setCell(0, 0, 49 + procedureclass.cyfra(buff_czas, 1));
       buff_napis.setCell(1, 0, 49 + procedureclass.cyfra(buff_czas, 0));
       buff_napis.setPosition(width - 48 - 3 + (i * 12), 28 + 12 - 3);
       buff_napis.paint(g);
      }
      if ((i >= 4) && (i < 8)) {
       if (buff_czas > 9) buff_napis.setCell(0, 0, 49 + procedureclass.cyfra(buff_czas, 1));
       buff_napis.setCell(1, 0, 49 + procedureclass.cyfra(buff_czas, 0));
       buff_napis.setPosition(width - 48 - 3 + ((i - 4) * 12), 28 + 24 - 3);
       buff_napis.paint(g);
      }
     }
    }
   }
   inventory_menu.setPosition(width - (12 * 5) - 3, height - 17);
   inventory_menu.paint(g);
   if (quest_pokaz == 1) {
    for (j = 0; j < 15; j++) {
     for (i = 0; i < 5; i++) {
      itemy_inv.setCell(i, j, 0);
     }
    }
    for (j = 0; j < 17; j++) {
     for (i = 0; i < 12; i++) {
      inventory_b.setCell(i, j, 0);
     }
    }
    for (i = 0; i < 6; i++)
     inventory_b.setCell(i, 0, i + 1);
    for (i = 6; i < 12; i++)
     inventory_b.setCell(i, 0, i - 4);
    for (j = 1; j < 10; j++) {
     for (i = 0; i < 12; i++) {
      switch (i) {
       case 0:
        inventory_b.setCell(i, j, 15);
        break;
       case 1:
        inventory_b.setCell(i, j, 36);
        break;
       case 2:
        inventory_b.setCell(i, j, 36);
        break;
       case 3:
        inventory_b.setCell(i, j, 36);
        break;
       case 4:
        inventory_b.setCell(i, j, 36);
        break;
       case 5:
        inventory_b.setCell(i, j, 36);
        break;
       case 6:
        inventory_b.setCell(i, j, 36);
        break;
       case 7:
        inventory_b.setCell(i, j, 36);
        break;
       case 8:
        inventory_b.setCell(i, j, 36);
        break;
       case 9:
        inventory_b.setCell(i, j, 36);
        break;
       case 10:
        inventory_b.setCell(i, j, 36);
        break;
       case 11:
        inventory_b.setCell(i, j, 21);
        break;
      }
     }
    }
    for (i = 0; i < 12; i++) {
     switch (i) {
      case 0:
       inventory_b.setCell(i, 2, 15);
       break;
      case 1:
       inventory_b.setCell(i, 2, 57);
       break;
      case 2:
       inventory_b.setCell(i, 2, 23);
       break;
      case 3:
       inventory_b.setCell(i, 2, 23);
       break;
      case 4:
       inventory_b.setCell(i, 2, 23);
       break;
      case 5:
       inventory_b.setCell(i, 2, 62);
       break;
      case 6:
       inventory_b.setCell(i, 2, 23);
       break;
      case 7:
       inventory_b.setCell(i, 2, 23);
       break;
      case 8:
       inventory_b.setCell(i, 2, 23);
       break;
      case 9:
       inventory_b.setCell(i, 2, 58);
       break;
      case 10:
       inventory_b.setCell(i, 2, 23);
       break;
      case 11:
       inventory_b.setCell(i, 2, 21);
       break;
     }
    }
    for (j = 1; j < 10; j++) {
     switch (j) {
      case 1:
       inventory_b.setCell(10, j, 36);
       break;
      case 2:
       inventory_b.setCell(10, j, 36);
       break;
      case 3:
       inventory_b.setCell(10, j, 59);
       break;
      case 4:
       inventory_b.setCell(10, j, 23);
       break;
      case 5:
       inventory_b.setCell(10, j, 23);
       break;
      case 6:
       inventory_b.setCell(10, j, 23);
       break;
      case 7:
       inventory_b.setCell(10, j, 23);
       break;
      case 8:
       inventory_b.setCell(10, j, 23);
       break;
      case 9:
       inventory_b.setCell(10, j, 60);
       break;
     }
    }
    for (i = 0; i < 7; i++)
     inventory_b.setCell(i, 10, i + 29);

    inventory_b.setPosition(width - (12 * 12) + 6 - 12, height - (12 * (11)) + 7);
    inventory_b.paint(g);

    //gracz_quest_moby_rodzaje[0-9] - ilosc rodzajow mobkow do ubicia ; gracz_quest_id[0-9]>0


    while ((gracz_quest_id[quest_menu_nr] == 0) && (quest_menu_nr > 1)) quest_menu_nr--;
    //tytul_quest= "                               ".toCharArray();
    if (quest_loaded == 0) {
     try {
      InputStream in = this.getClass().getResourceAsStream("quest_text/1.txt");
      DataInputStream datastream = new DataInputStream( in );
      c = 0;
      m = 0; //zmienna liczaca ktory bajt z rzedu czyta gra
      while (((c = in .read()) != '\n') && (c != -1)) {
       if (m < 100)
        tytul_quest[m] = ((char) c); //zapisz dany znak do bufora
       m++;
      }
      quest_title_bytes = m;
      m = 0;
      while (((c = in .read()) != '\n') && (c != -1)) {
       if (m < 1000)
        tresc_quest[m] = ((char) c); //zapisz dany znak do bufora
       m++;
      }
      int szukana_pozycja = 0;
      ilosc_linii_q = 0;
      for (j = 0; szukana_pozycja < m; j = j + 27) {
       for (k = j; k < (j + 27); k++) {
        if (szukana_pozycja < m) {
         if ((k == j) && (tresc_quest[szukana_pozycja] == 32)) szukana_pozycja++;
         info_quest[k] = tresc_quest[szukana_pozycja];
         szukana_pozycja++;
        } else info_quest[k] = 32;
       }
       k = j + 26;
       if (szukana_pozycja < m) {
        while (tresc_quest[szukana_pozycja] != 32) {
         info_quest[k] = 32;
         k--;
         szukana_pozycja--;
        }
       }
       ilosc_linii_q++;
      }
     } catch (Exception e) {
      e.printStackTrace();
     }
     quest_loaded = quest_menu_nr;
    }

    i = 0;
    for (j = 0; j < 30; j++) {
     if (j < quest_title_bytes) {
      info_c[j] = tytul_quest[j];
     } else info_c[j] = 32;
    }
    c = 29;
    while (info_c[c] != 32) {
     info_c[c] = 32;
     c--;
    }
    c++;
    //info_c =   " (1) TESTOWA NAZWA QUESTA    1".toCharArray();
    nap_itemy[i] = new TiledLayer(30, 1, fontbw, 4, 6);
    nap_itemy[i + 1] = new TiledLayer(30, 1, fontblack, 4, 6);
    for (j = 0; j < 30; j++) {
     nap_itemy[i].setCell(j, 0, info_c[j] + 1);
     nap_itemy[i + 1].setCell(j, 0, info_c[j] + 1);
    }
    i = i + 2;
    for (j = c; j < (c + 30); j++) {
     if (j < quest_title_bytes) {
      info_c[j - c] = tytul_quest[j];
     } else info_c[j - c] = 32;
    }
    c = 29;
    while (info_c[c] != 32) {
     info_c[c] = 32;
     c--;
    }
    nap_itemy[i] = new TiledLayer(30, 1, fontbw, 4, 6);
    nap_itemy[i + 1] = new TiledLayer(30, 1, fontblack, 4, 6);
    for (j = 0; j < 30; j++) {
     nap_itemy[i].setCell(j, 0, info_c[j] + 1);
     nap_itemy[i + 1].setCell(j, 0, info_c[j] + 1);
    }
    i = i + 2;

    for (k = 0; k < i; k = k + 2) {
     nap_itemy[k + 1].setPosition(width - (12 * 12) + 7, height - (12 * (11)) + 19 + (k * 3) + 1);
     nap_itemy[k + 1].paint(g);
     nap_itemy[k].setPosition(width - (12 * 12) + 6, height - (12 * (11)) + 19 + (k * 3));
     nap_itemy[k].paint(g);
    }

    //i=i+4; //przerwa na pasek (strzalki i X)
    i = 0;
    nap_itemy[0] = new TiledLayer(30, 14, fontbw, 4, 6);
    nap_itemy[1] = new TiledLayer(30, 14, fontblack, 4, 6);
    for (k = 0; k < 14; k++) {
     info_c = "                              ".toCharArray(); //30 znakow
     for (j = 0; j < 27; j++) {
      nap_itemy[0].setCell(j, k, info_quest[j + (k * 27) + (27 * pozycja_linii_q)] + 1);
      nap_itemy[1].setCell(j, k, info_quest[j + (k * 27) + (27 * pozycja_linii_q)] + 1);
     }
    }
    nap_itemy[1].setPosition(width - (12 * 12) + 7, height - (12 * (11)) + 19 + 24 + 1);
    nap_itemy[1].paint(g);
    nap_itemy[0].setPosition(width - (12 * 12) + 6, height - (12 * (11)) + 19 + 24 + 0);
    nap_itemy[0].paint(g);

    //for(k=0;k<i;k=k+2) {
    //}

    nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
    nap_itemy[1] = new TiledLayer(30, 1, fontblack, 4, 6);
    info_c = "CURRENT QUESTS (01/10)        ".toCharArray();
    for (j = 0; j < 30; j++) {
     nap_itemy[0].setCell(j, 0, info_c[j] + 1);
     nap_itemy[1].setCell(j, 0, info_c[j] + 1);
    }
    nap_itemy[1].setPosition(width - (12 * 12) + 6 - 1, height - (12 * (11)) + 19 - 8);
    nap_itemy[1].paint(g);
    nap_itemy[1].setPosition(width - (12 * 12) + 6 + 1, height - (12 * (11)) + 19 - 8);
    nap_itemy[1].paint(g);
    nap_itemy[1].setPosition(width - (12 * 12) + 6, height - (12 * (11)) + 19 - 8 - 1);
    nap_itemy[1].paint(g);
    nap_itemy[1].setPosition(width - (12 * 12) + 6, height - (12 * (11)) + 19 - 8 + 1);
    nap_itemy[1].paint(g);
    nap_itemy[0].setPosition(width - (12 * 12) + 6, height - (12 * (11)) + 19 - 8);
    nap_itemy[0].paint(g);

    menu_temp_x = menu_x * 4;
    if (menu_y != 2) menu_temp_y = menu_y + 1;
    else menu_temp_y = 8;
    if (menu_y != 0) menu_temp_x++;
    kursor_menu.setPosition(width - (11 * 12) + 6 + (menu_temp_x * 12) - 12, height - (12 * (7 + 3)) + 7 + (menu_temp_y * 12));
    kursor_menu.paint(g);
   }
   if (char_pokaz == 1) {
    ilosc_linii_inv = 7;
    for (j = 0; j < 15; j++) {
     for (i = 0; i < 5; i++) {
      itemy_inv.setCell(i, j, 0);
     }
    }
    for (j = 0; j < 17; j++) {
     for (i = 0; i < 12; i++) {
      inventory_b.setCell(i, j, 0);
     }
    }
    for (i = 0; i < 7; i++) {
     inventory_b.setCell(i, 0, i + 1);
    }
    for (j = 1; j < 8; j++) {
     for (i = 0; i < 7; i++) {
      switch (i) {
       case 0:
        inventory_b.setCell(i, j, 15);
        break;
       case 1:
        inventory_b.setCell(i, j, 16);
        break;
       case 2:
        inventory_b.setCell(i, j, 42 + j);
        break;
       case 3:
        inventory_b.setCell(i, j, 35 + 1);
        break;
       case 4:
        inventory_b.setCell(i, j, 42 + 7 + j);
        break;
       case 5:
        inventory_b.setCell(i, j, 20);
        break;
       case 6:
        inventory_b.setCell(i, j, 21);
        break;
      }
     }
    }
    for (i = 0; i < 7; i++)
     inventory_b.setCell(i, 8, i + 29);
    for (j = 0; j < ilosc_linii_inv; j++) {
     i = 0; //lewa kolumna
     itemy_inv.setCell(i, j, equ_grafika[j]);
     i = 4; //prawa kolumna
     if (j != (ilosc_linii_inv - 1))
      itemy_inv.setCell(i, j, equ_grafika[j + 14]);
     else
      itemy_inv.setCell(i, j, equ_grafika[10]);
    }
    inventory_b.setPosition(width - (7 * 12) + 6 - 12, height - (12 * (9)) + 7);
    inventory_b.paint(g);
    itemy_inv.setPosition(width - (6 * 12) + 6 - 12, height - (12 * (ilosc_linii_inv + 1)) + 7);
    itemy_inv.paint(g);
    kursor_menu.setPosition(width - (6 * 12) + 6 + (menu_x * 12) - 12, height - (12 * (7 + 1)) + 7 + (menu_y * 12));
    kursor_menu.paint(g);
    //////////////////////////////////////////
    if (equip_arr[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
     for (j = 0; j < 9; j++) {
      for (i = 0; i < 14; i++)
       item_info_b.setCell(i, j, 0);
     }
     if (equ_loaded[procedureclass.przelicz_menu_char(menu_x, menu_y)] == 0) {
      inv_zaladuj = equip_arr[procedureclass.przelicz_menu_char(menu_x, menu_y)];
      info_c = "RETRIEVING ITEM INFORMATION...".toCharArray();
      nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
      for (i = 0; i < 30; i++)
       nap_itemy[0].setCell(i, 0, info_c[i] + 1);
      i = 1;
     } else {
      switch (equ_wartosc[procedureclass.przelicz_menu_char(menu_x, menu_y)]) {
       case 0:
        nap_itemy[0] = new TiledLayer(30, 1, fontgray, 4, 6);
        break;
       case 1:
        nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
        break;
       case 2:
        nap_itemy[0] = new TiledLayer(30, 1, fontgreen, 4, 6);
        break;
       case 3:
        nap_itemy[0] = new TiledLayer(30, 1, fontblue, 4, 6);
        break;
       case 4:
        nap_itemy[0] = new TiledLayer(30, 1, fontred, 4, 6);
        break;
       case 5:
        nap_itemy[0] = new TiledLayer(30, 1, fontviolet, 4, 6);
        break;
       case 6:
        nap_itemy[0] = new TiledLayer(30, 1, fontyellow, 4, 6);
        break;
      }
      for (i = 0; i < 30; i++) {
       nap_itemy[0].setCell(i, 0, equ_nazwa[procedureclass.przelicz_menu_char(menu_x, menu_y)][i] + 1);
      }
      nap_itemy[0].setPosition(width - (6 * 12) + 6 - 112, height - (12 * (ilosc_linii_inv + 2)) + 12);
      int typ_itemu = equ_typ[procedureclass.przelicz_menu_char(menu_x, menu_y)];
      if ((typ_itemu > 19) && (typ_itemu < 40)) typ_itemu = 20;
      i = 1;
      switch (typ_itemu) {
       case 1: //consumable
        nap_itemy[1] = new TiledLayer(30, 1, fontgray, 4, 6);
        info_c = "RESTORES   0 HP &   0 MP /4SECS".toCharArray();
        for (i = 0; i < 30; i++) {
         nap_itemy[1].setCell(i, 0, info_c[i] + 1);
        }
        if (equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[1].setCell(9, 0, 49 + procedureclass.cyfra(equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
        nap_itemy[1].setCell(10, 0, 49 + procedureclass.cyfra(equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
        if (equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[1].setCell(18, 0, 49 + procedureclass.cyfra(equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
        nap_itemy[1].setCell(19, 0, 49 + procedureclass.cyfra(equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
        i = 2;
        break;
       case 20: //armor i bronie
        nap_itemy[1] = new TiledLayer(30, 1, fontgray, 4, 6);
        switch (equ_typ[procedureclass.przelicz_menu_char(menu_x, menu_y)]) {
         case 20:
          info_c = "HEAD                          ".toCharArray();
          break;
         case 21:
          info_c = "NECKLACE                      ".toCharArray();
          break;
         case 22:
          info_c = "SHOULDER                      ".toCharArray();
          break;
         case 23:
          info_c = "CHEST ARMOR                   ".toCharArray();
          break;
         case 24:
          info_c = "BRACER                        ".toCharArray();
          break;
         case 25:
          info_c = "GLOVES                        ".toCharArray();
          break;
         case 26:
          info_c = "RING                          ".toCharArray();
          break;
         case 34:
          info_c = "BELT                          ".toCharArray();
          break;
         case 35:
          info_c = "PANTS                         ".toCharArray();
          break;
         case 36:
          info_c = "BOOTS                         ".toCharArray();
          break;
         case 37:
          info_c = "CLOAK                         ".toCharArray();
          break;
         case 38:
          info_c = "WEAPON                        ".toCharArray();
          break;
         case 39:
          info_c = "SHIELD                        ".toCharArray();
          break;
        }
        for (i = 0; i < 30; i++) {
         nap_itemy[1].setCell(i, 0, info_c[i] + 1);
        }
        i = 2;
        if (equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         if (gracz_lvl < equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)]) nap_itemy[i] = new TiledLayer(30, 1, fontred, 4, 6);
         else nap_itemy[i] = new TiledLayer(30, 1, fontbw, 4, 6);
         info_c = "REQUIRES LVL                  ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_dura[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgray, 4, 6);
         info_c = "ARMOR     :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         //for(j=0;j<4;j++)	nap_itemy[i].setCell(12+j,0,49+procedureclass.cyfra(equ_arm[procedureclass.przelicz_menu_char(menu_x,menu_y)],3-j));
         if (equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_arm[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontlblue, 4, 6);
         info_c = "MAGIC DEF :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_md[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontlblue, 4, 6);
         info_c = "BONUS DMG :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_bd[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontlblue, 4, 6);
         info_c = "BONUS HEAL:                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_bh[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "INTELECT  :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_int[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "SPIRIT    :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_spi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "STAMINA   :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_sta[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "AGILITY   :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_agi[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        if (equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "STRENGHT  :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)], 3));
         if (equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)], 2));
         if (equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(equ_str[procedureclass.przelicz_menu_char(menu_x, menu_y)], 0));
         i++;
        }
        break;
      }
     }
     j = 0;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, j, 1 + k + (j * 12));
     j = 1;
     for (l = 0; l < ((i + 1) / 2); l++) {
      for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));
     }
     j = 2;
     l = (i + 1) / 2;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));
     int item_info_x = width - (7 * 12) + 2 - 124 + (menu_x * 12) - 4;
     int item_info_y = height - (12 * (ilosc_linii_inv + 2)) - ((l - 1) * 12) - 4;
     if (item_info_x < -10) item_info_x = -10;
     if (item_info_y < -10) item_info_y = -10;
     item_info_b.setPosition(item_info_x, item_info_y);
     item_info_b.paint(g);
     for (k = 0; k < i; k++) {
      nap_itemy[k].setPosition(item_info_x + 12, item_info_y + 12 + (k * 6));
      nap_itemy[k].paint(g);
     }
    } /////////////////////
   }
   if (menu_pokaz == 1) {
    for (j = 0; j < 9; j++) {
     for (i = 0; i < 12; i++) {
      item_info_b.setCell(i, j, 0);
     }
    }
    kursor_menu.setPosition(width - (12 * 5) - 3 + (menu_x * 12), height - 17);
    kursor_menu.paint(g);
    i = 1;
    j = 0;
    for (k = 0; k < 12; k++) item_info_b.setCell(k, j, 1 + k + (j * 12));
    j = 1;
    l = 0;
    for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));
    j = 2;
    l = (i + 1) / 2;
    for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));

    int item_info_x = width - (12 * 5) - 3 + (menu_x * 12) - 134;
    int item_info_y = height - 17 - 26;
    if (item_info_x < -10) item_info_x = -10;
    if (item_info_y < -10) item_info_y = -10;
    item_info_b.setPosition(item_info_x, item_info_y);
    item_info_b.paint(g);
    switch (menu_x) {
     case 0:
      info_c = "QUESTS                        ".toCharArray();
      break;
     case 1:
      info_c = "TALENTS                       ".toCharArray();
      break;
     case 2:
      info_c = "SPELLBOOK                     ".toCharArray();
      break;
     case 3:
      info_c = "EQUIPMENT                     ".toCharArray();
      break;
     case 4:
      info_c = "INVENTORY                     ".toCharArray();
      break;
    }
    nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
    for (i = 0; i < 30; i++)
     nap_itemy[0].setCell(i, 0, info_c[i] + 1);
    if ((menu_x < 3) && (menu_x > 0)) {
     info_c = "NOT YET AVALIABLE             ".toCharArray();
    } else {
     info_c = "                              ".toCharArray();
    }
    nap_itemy[1] = new TiledLayer(30, 1, fontred, 4, 6);
    for (i = 0; i < 30; i++)
     nap_itemy[1].setCell(i, 0, info_c[i] + 1);
    for (k = 0; k < 2; k++) {
     nap_itemy[k].setPosition(item_info_x + 12, item_info_y + 12 + (k * 6));
     nap_itemy[k].paint(g);
    }
   }

   //ilosc kb przed menu (menu zakrywa ilosc odebranych danych)
   g.setColor(255, 255, 255);
   g.drawString("" + debug, 2, 2, Graphics.TOP | Graphics.LEFT);

   if (inventory_pokaz == 1) {
    inventory_cap = 15 + inventory_bagi[0] + inventory_bagi[1] + inventory_bagi[2] + inventory_bagi[3];
    ilosc_linii_inv = inventory_cap / 5;
    int ilosc_w_ostatniej = inventory_cap - (ilosc_linii_inv * 5);
    if (ilosc_w_ostatniej > 0) ilosc_linii_inv++;
    for (j = 0; j < 15; j++) {
     for (i = 0; i < 5; i++) {
      itemy_inv.setCell(i, j, 0);
     }
    }
    for (j = 0; j < 17; j++) {
     for (i = 0; i < 12; i++) {
      inventory_b.setCell(i, j, 0);
     }
    }
    for (i = 0; i < 7; i++)
     inventory_b.setCell(i, 0, i + 1);
    for (j = 0; j < ilosc_linii_inv; j++) {
     for (i = 0; i < 7; i++) {
      inventory_b.setCell(i, (j + 1), i + 8);
      if (((j + 1) == ilosc_linii_inv) && ((i) > ilosc_w_ostatniej) && ((i + 1) != 7) && (ilosc_w_ostatniej != 0))
       inventory_b.setCell(i, (j + 1), 37);
     }
    }
    for (i = 0; i < 7; i++)
     inventory_b.setCell(i, ilosc_linii_inv + 1, i + 29);
    inventory_b.setPosition(width - (7 * 12) + 6, height - (12 * (ilosc_linii_inv + 2)) + 7);
    inventory_b.paint(g);
    for (j = 0; j < ilosc_linii_inv; j++) {
     for (i = 0; i < 5; i++) {
      itemy_inv.setCell(i, j, inv_grafika[(j * 5) + i]);
      int ilosc_100 = inventory_arr_ilosc[(j * 5) + i] / 100;
      int ilosc_10 = (inventory_arr_ilosc[(j * 5) + i] / 10) - (ilosc_100 * 10);
      int ilosc_1 = (inventory_arr_ilosc[(j * 5) + i]) - (ilosc_10 * 10) - (ilosc_100 * 100);
      if (ilosc_100 > 0) itemy_inv_ilosc.setCell((i * 3) + 0, (j * 2) + 1, 49 + ilosc_100);
      else itemy_inv_ilosc.setCell((i * 3) + 0, (j * 2) + 1, 0);
      if (ilosc_10 > 0) itemy_inv_ilosc.setCell((i * 3) + 1, (j * 2) + 1, 49 + ilosc_10);
      else itemy_inv_ilosc.setCell((i * 3) + 1, (j * 2) + 1, 0);
      if ((ilosc_1 > 1) || (inventory_arr_ilosc[(j * 5) + i] > 1)) itemy_inv_ilosc.setCell((i * 3) + 2, (j * 2) + 1, 49 + ilosc_1);
      else itemy_inv_ilosc.setCell((i * 3) + 2, (j * 2) + 1, 0);
     }
    }
    itemy_inv.setPosition(width - (6 * 12) + 6, height - (12 * (ilosc_linii_inv + 1)) + 7);
    itemy_inv_ilosc.setPosition(width - (6 * 12) + 6, height - (12 * (ilosc_linii_inv + 1)) + 7);
    itemy_inv.paint(g);
    itemy_inv_ilosc.paint(g);
    kursor_menu.setPosition(width - (6 * 12) + 6 + (menu_x * 12), height - (12 * (ilosc_linii_inv + 1)) + 7 + (menu_y * 12));
    kursor_menu.paint(g);
    if (inventory_arr[menu_x + (menu_y * 5)] > 0) {
     for (j = 0; j < 9; j++) {
      for (i = 0; i < 14; i++)
       item_info_b.setCell(i, j, 0);
     }
     if (inv_loaded[menu_x + (menu_y * 5)] == 0) {
      inv_zaladuj = inventory_arr[menu_x + (menu_y * 5)];
      info_c = "RETRIEVING ITEM INFORMATION...".toCharArray();
      nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
      for (i = 0; i < 30; i++)
       nap_itemy[0].setCell(i, 0, info_c[i] + 1);
      //nap_itemy[0].setPosition(width-(6*12)+6-112, height-(12*(ilosc_linii_inv+2))+12);
      //nap_itemy[0].paint(g);
      i = 1;
     } else {
      switch (inv_wartosc[menu_x + (menu_y * 5)]) {
       case 0:
        nap_itemy[0] = new TiledLayer(30, 1, fontgray, 4, 6);
        break;
       case 1:
        nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
        break;
       case 2:
        nap_itemy[0] = new TiledLayer(30, 1, fontgreen, 4, 6);
        break;
       case 3:
        nap_itemy[0] = new TiledLayer(30, 1, fontblue, 4, 6);
        break;
       case 4:
        nap_itemy[0] = new TiledLayer(30, 1, fontred, 4, 6);
        break;
       case 5:
        nap_itemy[0] = new TiledLayer(30, 1, fontviolet, 4, 6);
        break;
       case 6:
        nap_itemy[0] = new TiledLayer(30, 1, fontyellow, 4, 6);
        break;
      }
      for (i = 0; i < 30; i++) {
       nap_itemy[0].setCell(i, 0, inv_nazwa[menu_x + (menu_y * 5)][i] + 1);
      }
      nap_itemy[0].setPosition(width - (6 * 12) + 6 - 112, height - (12 * (ilosc_linii_inv + 2)) + 12);
      int typ_itemu = inv_typ[menu_x + (menu_y * 5)];
      if ((typ_itemu > 19) && (typ_itemu < 40)) typ_itemu = 20;
      i = 1;
      switch (typ_itemu) {
       case 1: //consumable
        nap_itemy[1] = new TiledLayer(30, 1, fontgray, 4, 6);
        info_c = "RESTORES   0 HP &   0 MP /4SECS".toCharArray();
        for (i = 0; i < 30; i++) {
         nap_itemy[1].setCell(i, 0, info_c[i] + 1);
        }
        if (inv_sta[menu_x + (menu_y * 5)] > 9) nap_itemy[1].setCell(9, 0, 49 + procedureclass.cyfra(inv_sta[menu_x + (menu_y * 5)], 1));
        nap_itemy[1].setCell(10, 0, 49 + procedureclass.cyfra(inv_sta[menu_x + (menu_y * 5)], 0));
        if (inv_int[menu_x + (menu_y * 5)] > 9) nap_itemy[1].setCell(18, 0, 49 + procedureclass.cyfra(inv_int[menu_x + (menu_y * 5)], 1));
        nap_itemy[1].setCell(19, 0, 49 + procedureclass.cyfra(inv_int[menu_x + (menu_y * 5)], 0));
        i = 2;
        break;
       case 20: //armor i bronie
        nap_itemy[1] = new TiledLayer(30, 1, fontgray, 4, 6);
        switch (inv_typ[menu_x + (menu_y * 5)]) {
         case 20:
          info_c = "HEAD                          ".toCharArray();
          break;
         case 21:
          info_c = "NECKLACE                      ".toCharArray();
          break;
         case 22:
          info_c = "SHOULDER                      ".toCharArray();
          break;
         case 23:
          info_c = "CHEST ARMOR                   ".toCharArray();
          break;
         case 24:
          info_c = "BRACER                        ".toCharArray();
          break;
         case 25:
          info_c = "GLOVES                        ".toCharArray();
          break;
         case 26:
          info_c = "RING                          ".toCharArray();
          break;
         case 34:
          info_c = "BELT                          ".toCharArray();
          break;
         case 35:
          info_c = "PANTS                         ".toCharArray();
          break;
         case 36:
          info_c = "BOOTS                         ".toCharArray();
          break;
         case 37:
          info_c = "CLOAK                         ".toCharArray();
          break;
         case 38:
          info_c = "WEAPON                        ".toCharArray();
          break;
         case 39:
          info_c = "SHIELD                        ".toCharArray();
          break;
        }
        for (i = 0; i < 30; i++) {
         nap_itemy[1].setCell(i, 0, info_c[i] + 1);
        }
        //							nap_itemy[1].setPosition(width-(6*12)+6-112, height-(12*(ilosc_linii_inv+2))+18);
        //							nap_itemy[1].paint(g);
        i = 2;
        if (inv_dura[menu_x + (menu_y * 5)] > 0) {
         if (gracz_lvl < inv_dura[menu_x + (menu_y * 5)]) nap_itemy[i] = new TiledLayer(30, 1, fontred, 4, 6);
         else nap_itemy[i] = new TiledLayer(30, 1, fontbw, 4, 6);
         info_c = "REQUIRES LVL                  ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_dura[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_dura[menu_x + (menu_y * 5)], 2));
         if (inv_dura[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_dura[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_dura[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_arm[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgray, 4, 6);
         info_c = "ARMOR     :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         //for(j=0;j<4;j++)	nap_itemy[i].setCell(12+j,0,49+procedureclass.cyfra(inv_arm[menu_x+(menu_y*5)],3-j));
         if (inv_arm[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_arm[menu_x + (menu_y * 5)], 3));
         if (inv_arm[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_arm[menu_x + (menu_y * 5)], 2));
         if (inv_arm[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_arm[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_arm[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_md[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontlblue, 4, 6);
         info_c = "MAGIC DEF :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_md[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_md[menu_x + (menu_y * 5)], 3));
         if (inv_md[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_md[menu_x + (menu_y * 5)], 2));
         if (inv_md[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_md[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_md[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_bd[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontlblue, 4, 6);
         info_c = "BONUS DMG :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_bd[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_bd[menu_x + (menu_y * 5)], 3));
         if (inv_bd[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_bd[menu_x + (menu_y * 5)], 2));
         if (inv_bd[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_bd[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_bd[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_bh[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontlblue, 4, 6);
         info_c = "BONUS HEAL:                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_bh[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_bh[menu_x + (menu_y * 5)], 3));
         if (inv_bh[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_bh[menu_x + (menu_y * 5)], 2));
         if (inv_bh[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_bh[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_bh[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_int[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "INTELECT  :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_int[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_int[menu_x + (menu_y * 5)], 3));
         if (inv_int[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_int[menu_x + (menu_y * 5)], 2));
         if (inv_int[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_int[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_int[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_spi[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "SPIRIT    :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_spi[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_spi[menu_x + (menu_y * 5)], 3));
         if (inv_spi[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_spi[menu_x + (menu_y * 5)], 2));
         if (inv_spi[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_spi[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_spi[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_sta[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "STAMINA   :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_sta[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_sta[menu_x + (menu_y * 5)], 3));
         if (inv_sta[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_sta[menu_x + (menu_y * 5)], 2));
         if (inv_sta[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_sta[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_sta[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_agi[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "AGILITY   :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_agi[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_agi[menu_x + (menu_y * 5)], 3));
         if (inv_agi[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_agi[menu_x + (menu_y * 5)], 2));
         if (inv_agi[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_agi[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_agi[menu_x + (menu_y * 5)], 0));
         i++;
        }
        if (inv_str[menu_x + (menu_y * 5)] > 0) {
         nap_itemy[i] = new TiledLayer(30, 1, fontgreen, 4, 6);
         info_c = "STRENGHT  :                   ".toCharArray(); //12
         for (j = 0; j < 30; j++) nap_itemy[i].setCell(j, 0, info_c[j] + 1);
         if (inv_str[menu_x + (menu_y * 5)] > 999) nap_itemy[i].setCell(12, 0, 49 + procedureclass.cyfra(inv_str[menu_x + (menu_y * 5)], 3));
         if (inv_str[menu_x + (menu_y * 5)] > 99) nap_itemy[i].setCell(13, 0, 49 + procedureclass.cyfra(inv_str[menu_x + (menu_y * 5)], 2));
         if (inv_str[menu_x + (menu_y * 5)] > 9) nap_itemy[i].setCell(14, 0, 49 + procedureclass.cyfra(inv_str[menu_x + (menu_y * 5)], 1));
         nap_itemy[i].setCell(15, 0, 49 + procedureclass.cyfra(inv_str[menu_x + (menu_y * 5)], 0));
         i++;
        }
        break;
      }
     }
     j = 0;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, j, 1 + k + (j * 12));
     j = 1;
     for (l = 0; l < ((i + 1) / 2); l++) {
      for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));
     }
     j = 2;
     l = (i + 1) / 2;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));
     int item_info_x = width - (6 * 12) + 2 - 124 + (menu_x * 12) - 4;
     int item_info_y = height - (12 * (ilosc_linii_inv + 2)) - ((l - 1) * 12) - 4;
     if (item_info_x < -10) item_info_x = -10;
     if (item_info_y < -10) item_info_y = -10;
     item_info_b.setPosition(item_info_x, item_info_y);
     item_info_b.paint(g);
     for (k = 0; k < i; k++) {
      nap_itemy[k].setPosition(item_info_x + 12, item_info_y + 12 + (k * 6));
      nap_itemy[k].paint(g);
     }
    }
    //discard_pokaz
    if (discard_pokaz == 1) {
     for (j = 0; j < 9; j++) {
      for (i = 0; i < 12; i++) {
       item_info_b.setCell(i, j, 0);
      }
     }
     i = 1;
     j = 0;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, j, 1 + k + (j * 12));
     j = 1;
     l = 0;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));
     j = 2;
     l = (i + 1) / 2;
     for (k = 0; k < 12; k++) item_info_b.setCell(k, l + 1, 1 + k + (j * 12));

     //zmienic koordy okna
     //int item_info_x=width-(12*4)-3+(menu_x*12)-134; int item_info_y=height-17-26;
     int item_info_x = (width / 2) - 54;
     int item_info_y = (height / 2) - 12;

     if (item_info_x < -10) item_info_x = -10;
     if (item_info_y < -10) item_info_y = -10;
     item_info_b.setPosition(item_info_x, item_info_y);
     item_info_b.paint(g);

     switch (discard_x) {
      case 0:
       info_c = "    YES               --      ".toCharArray();
       nap_itemy[1] = new TiledLayer(30, 1, fontred, 4, 6);
       break;
      case 1:
       info_c = "    ---               NO      ".toCharArray();
       nap_itemy[1] = new TiledLayer(30, 1, fontbw, 4, 6);
       break;
     }
     for (i = 0; i < 30; i++)
      nap_itemy[1].setCell(i, 0, info_c[i] + 1);
     info_c = "        DISCARD ITEM?         ".toCharArray();
     nap_itemy[0] = new TiledLayer(30, 1, fontbw, 4, 6);
     for (i = 0; i < 30; i++)
      nap_itemy[0].setCell(i, 0, info_c[i] + 1);
     for (k = 0; k < 2; k++) {
      nap_itemy[k].setPosition(item_info_x + 12, item_info_y + 12 + (k * 6));
      nap_itemy[k].paint(g);
     }
    }
   }

   if (odebrano_loot_czas > 0) {
    g.setColor(0, 0, 0);
    g.drawString("Recieved " + odebrano_loot + " items", (width / 2) - 49, (height / 2) - 19, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.drawString("Recieved " + odebrano_loot + " items.", (width / 2) - 50, (height / 2) - 20, Graphics.TOP | Graphics.LEFT);
   }
   if (exp_wyswietl > 0) {
    g.setColor(0, 0, 0);
    g.drawString("EXP: " + exp_wartosc, (width / 2) - 19, (height / 2) - 19, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.drawString("EXP: " + exp_wartosc, (width / 2) - 20, (height / 2) - 20, Graphics.TOP | Graphics.LEFT);
   }
   if ((gracz_hp == 0) && (dead_secs == 3)) {
    g.setColor(0, 0, 0);
    long temp_czasowa = dead_secs_end - System.currentTimeMillis();
    if (temp_czasowa < 0) temp_czasowa = 0;
    g.drawString("You are dead. " + (((int)(temp_czasowa)) / 1000) + " secs to revive.", 1, (height / 2) - 19, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.drawString("You are dead. " + (((int)(temp_czasowa)) / 1000) + " secs to revive.", 2, (height / 2) - 20, Graphics.TOP | Graphics.LEFT);
   }
   if ((gracz_hp == 0) && (dead_secs == 2)) {
    g.setColor(0, 0, 0);
    g.drawString("You are dead. Press A to revive.", 1, (height / 2) - 19, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.drawString("You are dead. Press A to revive.", 2, (height / 2) - 20, Graphics.TOP | Graphics.LEFT);
   }
   if ((dead_secs == 1)) {
    g.setColor(0, 0, 0);
    g.drawString("Reviving.", (width / 2) - 19, (height / 2) - 19, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.drawString("Reviving.", (width / 2) - 20, (height / 2) - 20, Graphics.TOP | Graphics.LEFT);
   }
   if (lvl_up > 0) {
    g.setColor(0, 0, 0);
    g.drawString("LEVEL UP!!", (width / 2) - 19, (height / 2) + 10, Graphics.TOP | Graphics.LEFT);
    g.setColor(255, 255, 255);
    g.drawString("LEVEL UP!!", (width / 2) - 20, (height / 2) + 10, Graphics.TOP | Graphics.LEFT);
   }

   g.setColor(255, 0, 0);

   czas_renderu = System.currentTimeMillis() - czas_renderu;
   debug2 = " " + czas_renderu;
   debug2 = "" + (pozycja_x / klocki) + "/" + (pozycja_y / klocki);
   if (koniec_casta == 1)
    koniec_casta = 0; //informaca o tym ze klatka ze zmiana animacji zostala "przedstawiona"
  } else {

  }
  layerManager.paint(g, 0, 0);
  flushGraphics();
 }

 private int losowa(int numer) {
  Random random = new Random();
  int tak = Math.abs(random.nextInt() % numer);
  return tak;
 }

 private int losowa2(int numer, int zm1, int zm2) {
  Random random = new Random();
  int tak = Math.abs((random.nextInt() + zm1 + zm2) % numer);
  return tak;
 }

 public int il_pozi(String fpath) { //petla ze starej gry OLAC - zczytuje z pliku ilosc poziomow i zwraca ja w formie int zostawiam jako przyklad
  int tymcz, tymcz2;
  int zwrot = 0;
  try {

   InputStream is = this.getClass().getResourceAsStream(fpath);
   DataInputStream ds = new DataInputStream(is);
   for (tymcz = 0; tymcz < 4; tymcz++) {
    byte bajt = ds.readByte();
    tymcz2 = (int) bajt;
    tymcz2 = tymcz2 - 48;
    if (tymcz == 0) {
     zwrot = zwrot + tymcz2 * (1000);
    }
    if (tymcz == 1) {
     zwrot = zwrot + tymcz2 * (100);
    }
    if (tymcz == 2) {
     zwrot = zwrot + tymcz2 * (10);
    }
    if (tymcz == 3) {
     zwrot = zwrot + tymcz2 * (1);
    }
   }

  } catch (Exception ex) {
   System.err.println("loading error : " + ex.getMessage());
  }
  return zwrot;
 }



 public void zaladujmape(String fpath) { //ladowanie pliku mapy
  int tymcz_x, tymcz_y;
  try {
   // open the file
   InputStream is = this.getClass().getResourceAsStream(fpath);
   DataInputStream ds = new DataInputStream(is);
   try {

    for (tymcz_x = 0; tymcz_x < 10000; tymcz_x++) {
     plansza[tymcz_x] = 0;
    }

    for (tymcz_y = 0; tymcz_y < plansza_y; tymcz_y++) { //40
     for (tymcz_x = 0; tymcz_x < plansza_x; tymcz_x++) { //80
      // read a tile index
      byte bajt = ds.readByte();

      int bajt_256 = (int) bajt;
      if (bajt_256 < 0) {
       bajt_256 = bajt_256 + 256;
      } else {
       bajt_256 = bajt_256 + 0;
      }

      bajt = ds.readByte();
      plansza[tymcz_x + (tymcz_y * plansza_x)] = (int) bajt;
      if (plansza[tymcz_x + (tymcz_y * plansza_x)] < 0) {
       plansza[tymcz_x + (tymcz_y * plansza_x)] = plansza[tymcz_x + (tymcz_y * plansza_x)] + 256;
      } else {
       plansza[tymcz_x + (tymcz_y * plansza_x)] = plansza[tymcz_x + (tymcz_y * plansza_x)] + 0;
      }
      plansza[tymcz_x + (tymcz_y * plansza_x)] = plansza[tymcz_x + (tymcz_y * plansza_x)] + (bajt_256 * 256);
     }
    }

   } catch (Exception ex) {
    System.err.println("map loading error 2 : " + ex.getMessage());
   }
   // close the file
   ds.close();
   ds = null;
   is = null;
  } catch (Exception ex) {
   System.err.println("map loading error : " + ex.getMessage());
  }

 }


 public int kolizje(int zm_x, int zm_y) {
  if ((zm_x > -1) && (zm_x < plansza_x) && (zm_y > -1) && (zm_y < plansza_y)) {
   if (((plansza[zm_x + (zm_y * plansza_x)] > 0) && (plansza[zm_x + (zm_y * plansza_x)] < 13)) || ((plansza[zm_x + (zm_y * plansza_x)] > 29) && (plansza[zm_x + (zm_y * plansza_x)] < (30 + 12))) || ((plansza[zm_x + (zm_y * plansza_x)] > 59) && (plansza[zm_x + (zm_y * plansza_x)] < (60 + 12))) || ((plansza[zm_x + (zm_y * plansza_x)] > 89) && (plansza[zm_x + (zm_y * plansza_x)] < (90 + 12)))) {
    return 1;
   } else {
    if (plansza_l3[zm_x + (zm_y * plansza_x)] > 0) {
     return 1;
    } else {
     return 0;
    }
   }
  } else {
   return 1;
  }
 }

 public void set_hp(int ehp, int ehp_max, int e_player, int e_linia) {
  int e_poz = 0;
  for (e_poz = 0; e_poz < 11; e_poz++) {
   hp_mp[e_player].setCell(e_poz, e_linia, 0);
  }
  e_poz = 0;
  if ((ehp > 99999)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 5));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 4));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 3));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 75 + 1);
   e_poz++;
  }
  if ((ehp > 9999) && (ehp < 100000)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 4));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 3));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 46 + 1);
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 2));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 75 + 1);
   e_poz++;
  }
  if ((ehp > 999) && (ehp < 10000)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 3));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 2));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 1));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 0));
   e_poz++;
  }
  if ((ehp > 99) && (ehp < 1000)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 2));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 1));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 0));
   e_poz++;
  }
  if ((ehp > 9) && (ehp < 100)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 1));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 0));
   e_poz++;
  }
  if ((ehp > (0 - 1)) && (ehp < 10)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp, 0));
   e_poz++;
  }
  hp_mp[e_player].setCell(e_poz, e_linia, 47 + 1);
  e_poz++;

  if ((ehp_max > 99999)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 5));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 4));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 3));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 75 + 1);
   e_poz++;
  }
  if ((ehp_max > 9999) && (ehp_max < 100000)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 4));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 3));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 46 + 1);
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 2));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 75 + 1);
   e_poz++;
  }
  if ((ehp_max > 999) && (ehp_max < 10000)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 3));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 2));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 1));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 0));
   e_poz++;
  }
  if ((ehp_max > 99) && (ehp_max < 1000)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 2));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 1));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 0));
   e_poz++;
  }
  if ((ehp_max > 9) && (ehp_max < 100)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 1));
   e_poz++;
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 0));
   e_poz++;
  }
  if ((ehp_max > (0 - 1)) && (ehp_max < 10)) {
   hp_mp[e_player].setCell(e_poz, e_linia, 49 + procedureclass.cyfra(ehp_max, 0));
   e_poz++;
  }
 }

 public void zmien_plansze(int plansza_nr, int nowa_x, int nowa_y) {
  switch (plansza_nr) {
   case 1:
    pozycja_x = nowa_x * klocki;
    pozycja_y = nowa_y * klocki;
    plansza_x = 80;
    plansza_y = 40;
    zaladujmape("/level/map_001_l4.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l4[i] = plansza[i];
    }
    zaladujmape("/level/map_001_l3.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l3[i] = plansza[i];
    }
    zaladujmape("/level/map_001_l2.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l2[i] = plansza[i];
    }
    zaladujmape("/level/map_001_l1.dat");
    ilosc_postaci = 1;
    nr_target = -1;
    nr_planszy = plansza_nr;
    break;
   case 2:
    pozycja_x = nowa_x * klocki;
    pozycja_y = nowa_y * klocki;
    plansza_x = 20;
    plansza_y = 10;
    zaladujmape("/level/map_002_l4.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l4[i] = plansza[i];
    }
    zaladujmape("/level/map_002_l3.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l3[i] = plansza[i];
    }
    zaladujmape("/level/map_002_l2.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l2[i] = plansza[i];
    }
    zaladujmape("/level/map_002_l1.dat");
    ilosc_postaci = 1;
    nr_target = -1;
    nr_planszy = plansza_nr;
    break;
   case 3:
    pozycja_x = nowa_x * klocki;
    pozycja_y = nowa_y * klocki;
    plansza_x = 80;
    plansza_y = 20;
    zaladujmape("/level/map_003_l4.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l4[i] = plansza[i];
    }
    zaladujmape("/level/map_003_l3.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l3[i] = plansza[i];
    }
    zaladujmape("/level/map_003_l2.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l2[i] = plansza[i];
    }
    zaladujmape("/level/map_003_l1.dat");
    ilosc_postaci = 1;
    nr_target = -1;
    nr_planszy = plansza_nr;
    break;
   case 4:
    pozycja_x = nowa_x * klocki;
    pozycja_y = nowa_y * klocki;
    plansza_x = 40;
    plansza_y = 80;
    zaladujmape("/level/map_004_l4.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l4[i] = plansza[i];
    }
    zaladujmape("/level/map_004_l3.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l3[i] = plansza[i];
    }
    zaladujmape("/level/map_004_l2.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l2[i] = plansza[i];
    }
    zaladujmape("/level/map_004_l1.dat");
    ilosc_postaci = 1;
    nr_target = -1;
    nr_planszy = plansza_nr;
    break;
   case 5:
    pozycja_x = nowa_x * klocki;
    pozycja_y = nowa_y * klocki;
    plansza_x = 20;
    plansza_y = 20;
    zaladujmape("/level/map_005_l4.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l4[i] = plansza[i];
    }
    zaladujmape("/level/map_005_l3.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l3[i] = plansza[i];
    }
    zaladujmape("/level/map_005_l2.dat");
    for (i = 0; i < 10000; i++) {
     plansza_l2[i] = plansza[i];
    }
    zaladujmape("/level/map_005_l1.dat");
    ilosc_postaci = 1;
    nr_target = -1;
    nr_planszy = plansza_nr;
    break;
  }
 }


 public void init_klawiatura() {
  key_pozycja = 0;
  na_klawiszu = 0;
  cykl = 0;
 }

 public int klawiatura(Graphics g, String pytanie, int max_key_input) {

  char[] pytanie_c = pytanie.toCharArray();
  int zwrot = 1;

  switch (nr_klawiatury) {
   case 0:
    g.drawImage(oskey, (getWidth() / 2) - 55, (getHeight()) - 40, Graphics.TOP | Graphics.LEFT); //wykejenie obrazka "glowna"
    poz_key_x = (getWidth() / 2) - 55;
    poz_key_y = (getHeight()) - 40;
    key_szer = 10;
    break;
   case 1:
    g.drawImage(oskey, (getWidth() / 2) - 88, (getHeight()) - 64, Graphics.TOP | Graphics.LEFT); //wykejenie obrazka "glowna"
    poz_key_x = (getWidth() / 2) - 88;
    poz_key_y = (getHeight()) - 64;
    key_szer = 16;
    break;
   case 2:
    g.drawImage(oskey, (getWidth() / 2) - 110, (getHeight()) - 80, Graphics.TOP | Graphics.LEFT); //wykejenie obrazka "glowna"
    poz_key_x = (getWidth() / 2) - 110;
    poz_key_y = (getHeight()) - 80;
    key_szer = 20;
    break;
  }

  g.setColor(0, 0, 0);
  g.fillRect(0, poz_key_y - 22, getWidth(), getHeight());
  g.drawImage(oskey, poz_key_x, poz_key_y, Graphics.TOP | Graphics.LEFT);
  g.setColor(255, 255, 255);
  g.drawString(">", (poz_key_x + (key_szer / 4)) + ((na_klawiszu - (((na_klawiszu) / 13) * 13)) * key_szer), poz_key_y + ((na_klawiszu / 13) * key_szer), Graphics.TOP | Graphics.LEFT);

  g.setColor(255, 63, 0);

  for (int nk = 0; nk < key_pozycja; nk++)
   napis_klawiatura.setCell(0 + nk, 2, key_input[nk] + 1);
  for (int nk = key_pozycja; nk < max_key_input; nk++)
   napis_klawiatura.setCell(0 + nk, 2, 32 + 1);

  for (int nk = 0; nk < pytanie.length(); nk++) //zapytanie
   napis_klawiatura.setCell(0 + nk, 0, pytanie_c[nk] + 1);

  napis_klawiatura.setPosition(poz_key_x, poz_key_y - 19);
  napis_klawiatura.paint(g);

  flushGraphics(); //przerzucenie tego co narysowalem z tylu do przodu (double buffor)

  if (kierunek > 0) cykl++;
  if (cykl > 4) cykl = 0; // szybkosc powtarzania przy poruszaniu kursorem
  input();
  if (k == 0) { //jesli wycisnieto klawisz zeruj kierunek i cykl
   kierunek = 0;
   cykl = 0;
  }

  if ((kierunek == 1) && cykl == 0) na_klawiszu -= 13;
  if ((kierunek == 2) && cykl == 0) na_klawiszu++;
  if ((kierunek == 3) && cykl == 0) na_klawiszu += 13;
  if ((kierunek == 4) && cykl == 0) na_klawiszu--;

  if (na_klawiszu == (0 - 1)) na_klawiszu = (13 * 1) - 3;
  if (na_klawiszu == ((13 * 1) - 1)) na_klawiszu = (13 * 2) - 3;
  if (na_klawiszu == ((13 * 1) - 2)) na_klawiszu = 0;
  if (na_klawiszu == ((13 * 2) - 1)) na_klawiszu = (13 * 3) - 3;
  if (na_klawiszu == ((13 * 2) - 2)) na_klawiszu = 13;
  if (na_klawiszu == ((13 * 3) - 1)) na_klawiszu = (13 * 4) - 3;
  if (na_klawiszu == ((13 * 3) - 2)) na_klawiszu = (13 * 2);
  if (na_klawiszu == ((13 * 3) - 1)) na_klawiszu = (13 * 4) - 3;
  if (na_klawiszu == ((13 * 4) - 2)) na_klawiszu = (13 * 3);

  if (na_klawiszu < 0) na_klawiszu = na_klawiszu + (13 * 4);
  if (na_klawiszu > ((4 * 13) - 3)) na_klawiszu = na_klawiszu - (13 * 4);

  if (fire_buf == 1) {
   switch (na_klawiszu) {
    case 0:
     key_input[key_pozycja] = 48 + 1;
     key_pozycja++;
     break; //1
    case 1:
     key_input[key_pozycja] = 48 + 2;
     key_pozycja++;
     break; //2
    case 2:
     key_input[key_pozycja] = 48 + 3;
     key_pozycja++;
     break; //3
    case 3:
     key_input[key_pozycja] = 48 + 4;
     key_pozycja++;
     break; //4
    case 4:
     key_input[key_pozycja] = 48 + 5;
     key_pozycja++;
     break; //5
    case 5:
     key_input[key_pozycja] = 48 + 6;
     key_pozycja++;
     break; //6
    case 6:
     key_input[key_pozycja] = 48 + 7;
     key_pozycja++;
     break; //7
    case 7:
     key_input[key_pozycja] = 48 + 8;
     key_pozycja++;
     break; //8
    case 8:
     key_input[key_pozycja] = 48 + 9;
     key_pozycja++;
     break; //9
    case 9:
     key_input[key_pozycja] = 48 + 0;
     key_pozycja++;
     break; //0
    case 10:
     key_pozycja--;
     break; //backspace

    case 13:
     key_input[key_pozycja] = 113;
     key_pozycja++;
     break; //q
    case 14:
     key_input[key_pozycja] = 119;
     key_pozycja++;
     break; //w
    case 15:
     key_input[key_pozycja] = 101;
     key_pozycja++;
     break; //e
    case 16:
     key_input[key_pozycja] = 114;
     key_pozycja++;
     break; //r
    case 17:
     key_input[key_pozycja] = 116;
     key_pozycja++;
     break; //t
    case 18:
     key_input[key_pozycja] = 121;
     key_pozycja++;
     break; //y
    case 19:
     key_input[key_pozycja] = 117;
     key_pozycja++;
     break; //u
    case 20:
     key_input[key_pozycja] = 105;
     key_pozycja++;
     break; //i
    case 21:
     key_input[key_pozycja] = 111;
     key_pozycja++;
     break; //o
    case 22:
     key_input[key_pozycja] = 112;
     key_pozycja++;
     break; //p
    case 23:
     zwrot = 0;
     break; //enter

    case 27:
     key_input[key_pozycja] = 97;
     key_pozycja++;
     break; //a
    case 28:
     key_input[key_pozycja] = 115;
     key_pozycja++;
     break; //s
    case 29:
     key_input[key_pozycja] = 100;
     key_pozycja++;
     break; //d
    case 30:
     key_input[key_pozycja] = 102;
     key_pozycja++;
     break; //f
    case 31:
     key_input[key_pozycja] = 103;
     key_pozycja++;
     break; //g
    case 32:
     key_input[key_pozycja] = 104;
     key_pozycja++;
     break; //h
    case 33:
     key_input[key_pozycja] = 106;
     key_pozycja++;
     break; //j
    case 34:
     key_input[key_pozycja] = 107;
     key_pozycja++;
     break; //k
    case 35:
     key_input[key_pozycja] = 108;
     key_pozycja++;
     break; //l
    case 36:
     break; //shift

    case 40:
     key_input[key_pozycja] = 122;
     key_pozycja++;
     break; //z
    case 41:
     key_input[key_pozycja] = 120;
     key_pozycja++;
     break; //x
    case 42:
     key_input[key_pozycja] = 99;
     key_pozycja++;
     break; //c
    case 43:
     key_input[key_pozycja] = 118;
     key_pozycja++;
     break; //v
    case 44:
     key_input[key_pozycja] = 98;
     key_pozycja++;
     break; //b
    case 45:
     key_input[key_pozycja] = 110;
     key_pozycja++;
     break; //n
    case 46:
     key_input[key_pozycja] = 109;
     key_pozycja++;
     break; //m
    case 47:
     key_input[key_pozycja] = 46;
     key_pozycja++;
     break; //.
    case 48:
     key_input[key_pozycja] = 32;
     key_pozycja++;
     break; //spacja
    case 49:
     key_input[key_pozycja] = 32;
     key_pozycja++;
     break; //spacja 2
   }
   fire_buf = 0;
   if (key_pozycja > max_key_input) key_pozycja = max_key_input;
   if (key_pozycja < 0) key_pozycja = 0;
  }
  return zwrot;
 }


}
