package com.hunhun.myticket.model;

public class Ticket {
    public String id, nama_wisata, lokasi, ketentuan, date_wisata, time_wisata;
    public int harga_tiket, jumlah_tiket, total_harga;

    public Ticket() {
    }

    public Ticket(String nama_wisata, String lokasi, String ketentuan, String date_wisata, String time_wisata, int harga_tiket, int jumlah_tiket, int total_harga) {
        this.nama_wisata = nama_wisata;
        this.lokasi = lokasi;
        this.ketentuan = ketentuan;
        this.date_wisata = date_wisata;
        this.time_wisata = time_wisata;
        this.harga_tiket = harga_tiket;
        this.jumlah_tiket = jumlah_tiket;
        this.total_harga = total_harga;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public int getJumlah_tiket() {
        return jumlah_tiket;
    }

    public void setJumlah_tiket(int jumlah_tiket) {
        this.jumlah_tiket = jumlah_tiket;
    }
}
