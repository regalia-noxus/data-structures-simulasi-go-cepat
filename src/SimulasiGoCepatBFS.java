/**
 * Implementasi Graph Peta Kota menggunakan Growable Array-Based Adjacency List
 * dan pencarian driver terdekat menggunakan algoritma BFS murni berbasis Array.
 * 
 */
class GraphPetaKota {
    private final int jumlahSimpul;      // Jumlah persimpangan (V)
    private int[][] tetangga;            // Adjacency list berbasis array 2D (growable per baris)
    private int[] jumlahTetangga;        // Menyimpan jumlah cabang jalan aktual di tiap simpul
    private static final int KAPASITAS_AWAL = 4; // Rata-rata persimpangan memiliki 3-4 cabang jalan

    // Constructor: Inisialisasi struktur adjacency list berbasis array biasa
    public GraphPetaKota(int jumlahSimpul) {
        this.jumlahSimpul = jumlahSimpul;
        this.tetangga = new int[jumlahSimpul][KAPASITAS_AWAL];
        this.jumlahTetangga = new int[jumlahSimpul]; // Default terinisialisasi 0 di Java
    }

    // Method internal untuk menambahkan sisi berarah dengan mekanisme Growable Array
    private void tambahSisiBerarah(int dari, int ke) {
        // Jika array tetangga pada simpul 'dari' sudah penuh, gandakan kapasitasnya (growable)
        if (jumlahTetangga[dari] == tetangga[dari].length) {
            int kapasitasBaru = tetangga[dari].length * 2;
            int[] arrayBaru = new int[kapasitasBaru];
            
            // Menyalin elemen lama ke array baru secara manual
            for (int i = 0; i < tetangga[dari].length; i++) {
                arrayBaru[i] = tetangga[dari][i];
            }
            // Mengarahkan referensi ke array baru yang lebih besar
            tetangga[dari] = arrayBaru;
        }

        // Masukkan simpul tetangga baru dan tambah penghitung derajat simpul
        tetangga[dari][jumlahTetangga[dari]] = ke;
        jumlahTetangga[dari]++;
    }

    // Menambahkan ruas jalan dua arah (Undirected Graph) antar persimpangan
    public void tambahJalanDuaArah(int simpangA, int simpangB) {
        tambahSisiBerarah(simpangA, simpangB);
        tambahSisiBerarah(simpangB, simpangA);
    }

    /**
     * (i) & (ii) Method BFS untuk mencari simpul driver terdekat dalam satuan jumlah hop.
     * Dibuat murni menggunakan array biasa untuk Queue FIFO dan status kunjungan.
     * 
     * @param asal         Simpul persimpangan tempat penumpang berada
     * @param adalahDriver Array boolean penanda keberadaan driver di tiap simpul
     * @return             ID simpul driver terdekat, atau -1 jika tidak ada driver terjangkau
     */
    public int bfsCariTerdekat(int asal, boolean[] adalahDriver) {
        // 1. Array penanda status kunjungan agar tidak terjebak cycle
        boolean[] sudahDikunjungi = new boolean[jumlahSimpul];

        // 2. Array untuk mencatat jarak hop dan melacak rute (parent) dari titik asal
        int[] jarakHop = new int[jumlahSimpul];
        int[] parent = new int[jumlahSimpul];
        for (int i = 0; i < jumlahSimpul; i++) {
            parent[i] = -1;
        }

        // 3. Implementasi Queue FIFO manual menggunakan array biasa
        // Ukuran maksimal antrean tidak akan melebihi jumlahSimpul (V) karena tiap simpul
        // hanya boleh masuk ke antrean maksimal satu kali.
        int[] queue = new int[jumlahSimpul];
        int front = 0; // Pointer depan untuk mengambil elemen (dequeue)
        int rear = 0;  // Pointer belakang untuk menambah elemen (enqueue)

        // Langkah Inisialisasi BFS: Masukkan titik asal ke dalam Queue
        sudahDikunjungi[asal] = true;
        jarakHop[asal] = 0;
        queue[rear] = asal;
        rear++; // Enqueue selesai

        System.out.println("\n--- Memulai Penelusuran BFS dari Simpul Penumpang [" + asal + "] ---");

        // Loop utama BFS selama antrean tidak kosong (front < rear)
        while (front < rear) {
            // Operasi Dequeue O(1): Ambil elemen terdepan dari antrean
            int simpulSaatIni = queue[front];
            front++;

            System.out.println("Memeriksa Simpul [" + simpulSaatIni + "] (Jarak: " 
                               + jarakHop[simpulSaatIni] + " hop)");

            // Cek apakah di persimpangan ini terdapat driver
            // Karena sifat level-by-level BFS, driver pertama yang ditemukan PASTI terdekat!
            if (adalahDriver[simpulSaatIni]) {
                System.out.println(">> DRIVER DITEMUKAN di Simpul [" + simpulSaatIni 
                                   + "] dengan jarak " + jarakHop[simpulSaatIni] + " hop!");
                cetakRute(asal, simpulSaatIni, parent);
                return simpulSaatIni;
            }

            // Telusuri semua tetangga dari simpulSaatIni menggunakan array adjacency list
            int totalCabang = jumlahTetangga[simpulSaatIni];
            for (int i = 0; i < totalCabang; i++) {
                int simpulTetangga = tetangga[simpulSaatIni][i];

                // Jika simpul tetangga belum pernah dikunjungi, tandai dan masukkan ke Queue
                if (!sudahDikunjungi[simpulTetangga]) {
                    sudahDikunjungi[simpulTetangga] = true;
                    jarakHop[simpulTetangga] = jarakHop[simpulSaatIni] + 1;
                    parent[simpulTetangga] = simpulSaatIni;

                    // Operasi Enqueue O(1)
                    queue[rear] = simpulTetangga;
                    rear++;
                }
            }
        }

        System.out.println(">> Maaf, tidak ada driver yang tersedia di jaringan jalan.");
        return -1;
    }

    // Method bantu berbasis array untuk menampilkan rute persimpangan yang dilalui
    private void cetakRute(int asal, int tujuan, int[] parent) {
        int[] rute = new int[jumlahSimpul];
        int panjangRute = 0;
        int temp = tujuan;

        while (temp != -1) {
            rute[panjangRute] = temp;
            panjangRute++;
            temp = parent[temp];
        }

        System.out.print("   Rute Persimpangan: ");
        for (int i = panjangRute - 1; i >= 0; i--) {
            System.out.print("[" + rute[i] + "]");
            if (i > 0) System.out.print(" -> ");
        }
        System.out.println();
    }

    // Menampilkan struktur Adjacency List peta kota
    public void tampilkanPetaKota() {
        System.out.println("=== Struktur Adjacency List Peta Kota (10 Persimpangan) ===");
        for (int i = 0; i < jumlahSimpul; i++) {
            System.out.print("Simpul [" + i + "] terhubung ke: ");
            for (int j = 0; j < jumlahTetangga[i]; j++) {
                System.out.print(tetangga[i][j] + " ");
            }
            System.out.println("(Total: " + jumlahTetangga[i] + " jalan)");
        }
    }
}

public class SimulasiGoCepatBFS {
    public static void main(String[] args) {
        // (iii) Simulasi dengan Graph berisi 10 simpul (Simpul 0 s/d 9, memenuhi syarat >= 8 simpul)
        int jumlahSimpul = 10;
        GraphPetaKota peta = new GraphPetaKota(jumlahSimpul);

        /*
         * Visualisasi Topologi Peta Kota (10 Simpul):
         * 
         *   [0] --- [1] --- [4 (DRIVER)]
         *    |       |       |
         *   [2] --- [3] --- [5] --- [8] --- [9 (DRIVER)]
         *            |               |
         *           [6] ----------- [7 (DRIVER)]
         */
        peta.tambahJalanDuaArah(0, 1);
        peta.tambahJalanDuaArah(0, 2);
        peta.tambahJalanDuaArah(1, 3);
        peta.tambahJalanDuaArah(1, 4);
        peta.tambahJalanDuaArah(2, 3);
        peta.tambahJalanDuaArah(3, 5);
        peta.tambahJalanDuaArah(3, 6);
        peta.tambahJalanDuaArah(4, 5);
        peta.tambahJalanDuaArah(5, 8);
        peta.tambahJalanDuaArah(6, 7);
        peta.tambahJalanDuaArah(7, 8);
        peta.tambahJalanDuaArah(8, 9);

        peta.tampilkanPetaKota();

        // Menempatkan beberapa Driver yang tersebar di Simpul 4, Simpul 7, dan Simpul 9
        boolean[] adalahDriver = new boolean[jumlahSimpul];
        adalahDriver[4] = true;
        adalahDriver[7] = true;
        adalahDriver[9] = true;
        System.out.println("\nPosisi Driver Aktif berada di Simpul: [4], [7], dan [9]");

        // SKENARIO 1: Penumpang pertama berada di Simpul 0
        int titikPenumpang1 = 0;
        int driverTerdekat1 = peta.bfsCariTerdekat(titikPenumpang1, adalahDriver);
        System.out.println("Hasil Akhir Skenario 1 (Penumpang di Simpul " + titikPenumpang1 
                           + "): Driver terdekat ada di Simpul [" + driverTerdekat1 + "]");

        // SKENARIO 2: Penumpang kedua berada di Simpul 6
        int titikPenumpang2 = 6;
        int driverTerdekat2 = peta.bfsCariTerdekat(titikPenumpang2, adalahDriver);
        System.out.println("Hasil Akhir Skenario 2 (Penumpang di Simpul " + titikPenumpang2 
                           + "): Driver terdekat ada di Simpul [" + driverTerdekat2 + "]");
    }
}
