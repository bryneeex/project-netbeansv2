-- ============================================
-- DATABASE: db_fardhan
-- Aplikasi Penggajian Karyawan - NetBeans
-- ============================================

CREATE DATABASE IF NOT EXISTS db_fardhan;
USE db_fardhan;

-- Tabel User (Login)
CREATE TABLE IF NOT EXISTS tb_user (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Data default login: admin / admin
INSERT INTO tb_user (username, password) VALUES ('admin', 'admin')
ON DUPLICATE KEY UPDATE username = username;

-- Tabel Golongan
CREATE TABLE IF NOT EXISTS tb_golongan (
    id_golongan VARCHAR(10) PRIMARY KEY,
    nama_golongan VARCHAR(50) NOT NULL,
    gaji_pokok DOUBLE NOT NULL DEFAULT 0,
    tunjangan_istri DOUBLE NOT NULL DEFAULT 0,
    jumlah_anak INT NOT NULL DEFAULT 0,
    tunjangan_anak DOUBLE NOT NULL DEFAULT 0,
    transport DOUBLE NOT NULL DEFAULT 0,
    uang_makan DOUBLE NOT NULL DEFAULT 0
);

-- Data contoh golongan
INSERT INTO tb_golongan VALUES ('G001', 'Golongan I',   3000000, 300000, 0, 0,       200000, 150000) ON DUPLICATE KEY UPDATE nama_golongan = nama_golongan;
INSERT INTO tb_golongan VALUES ('G002', 'Golongan II',  4000000, 400000, 1, 100000,  200000, 150000) ON DUPLICATE KEY UPDATE nama_golongan = nama_golongan;
INSERT INTO tb_golongan VALUES ('G003', 'Golongan III', 5000000, 500000, 2, 200000,  250000, 200000) ON DUPLICATE KEY UPDATE nama_golongan = nama_golongan;
INSERT INTO tb_golongan VALUES ('G004', 'Golongan IV',  7000000, 700000, 2, 300000,  300000, 250000) ON DUPLICATE KEY UPDATE nama_golongan = nama_golongan;

-- Tabel Karyawan
CREATE TABLE IF NOT EXISTS tb_karyawan (
    id_karyawan VARCHAR(10) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    id_golongan VARCHAR(10) NOT NULL,
    jenis_kelamin VARCHAR(15) NOT NULL,
    tempat_lahir VARCHAR(50),
    tanggal_lahir DATE,
    status VARCHAR(20) NOT NULL,
    alamat TEXT,
    FOREIGN KEY (id_golongan) REFERENCES tb_golongan(id_golongan)
);

-- Tabel Lembur
CREATE TABLE IF NOT EXISTS tb_lembur (
    id_lembur VARCHAR(10) PRIMARY KEY,
    id_karyawan VARCHAR(10) NOT NULL,
    tanggal_lembur DATE NOT NULL,
    jumlah_jam INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id_karyawan) REFERENCES tb_karyawan(id_karyawan)
);

-- Tabel Penggajian
CREATE TABLE IF NOT EXISTS tb_penggajian (
    id_gaji VARCHAR(10) PRIMARY KEY,
    tanggal_gaji DATE NOT NULL,
    id_karyawan VARCHAR(10) NOT NULL,
    nama_karyawan VARCHAR(100),
    golongan VARCHAR(50),
    jumlah_gaji DOUBLE NOT NULL DEFAULT 0,
    jumlah_lembur DOUBLE NOT NULL DEFAULT 0,
    potongan DOUBLE NOT NULL DEFAULT 0,
    total_gaji DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (id_karyawan) REFERENCES tb_karyawan(id_karyawan)
);
