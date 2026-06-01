# Aplikasi Penggajian Karyawan - Java NetBeans

Aplikasi Desktop Java (Swing) untuk manajemen penggajian karyawan.

## Struktur Project

Project ini sudah berisi semua `Source Code` (file `.java`) dan script Database.

* `src/penggajian/` : Berisi semua form GUI dan koneksi database.
* `sql/penggajian.sql` : Script database MySQL.

## Cara Import ke NetBeans IDE

Karena file konfigurasi internal NetBeans (`nbproject`) berbeda-beda tiap komputer, ikuti langkah berikut untuk menjalankan project ini di komputer Anda:

1. **Buat Database:**
   * Buka phpMyAdmin / MySQL (XAMPP).
   * Buat database baru bernama `db_fardhan`.
   * Import file `sql/penggajian.sql` ke dalam database tersebut.

2. **Buat Project Baru di NetBeans:**
   * Buka NetBeans IDE.
   * Pilih **File** -> **New Project** -> **Java with Ant** -> **Java Application**.
   * Beri nama project: `AplikasiPenggajian`. Jangan centang "Create Main Class" (atau hapus class utamanya nanti).
   * Klik **Finish**.

3. **Copy Source Code:**
   * Copy seluruh isi folder `src/penggajian` dari repository ini.
   * Paste ke dalam folder `src` di dalam project NetBeans yang baru Anda buat.

4. **Tambahkan Library (Wajib!):**
   * Klik kanan pada folder **Libraries** di project NetBeans Anda -> **Add JAR/Folder**.
   * Tambahkan `mysql-connector-java.jar` (Untuk koneksi database).
   * Tambahkan `jcalendar.jar` (Untuk fitur JDateChooser).
   * *Catatan: Jika Anda sudah menambahkan jcalendar, buka file `FormKaryawan.java`, `FormLembur.java`, dan `FormPenggajian.java`, lalu hapus komentar pada import `JDateChooser` dan ganti field text dengan komponen JDateChooser sesuai PRD.*

5. **Jalankan Aplikasi:**
   * Klik kanan pada file `Main.java` -> **Run File**.
   * Login default:
     * Username: `admin`
     * Password: `admin`

## Kolaborasi Tim (Git)

Jika ada update terbaru dari GitHub, jalankan perintah ini di Terminal/Command Prompt (pastikan berada di dalam folder project):
```bash
git pull origin main
```
Untuk menyimpan dan upload hasil kerja Anda:
```bash
git add .
git commit -m "Update form apa..."
git push origin main
```