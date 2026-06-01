@echo off
title Aplikasi Penggajian Karyawan - Runner
color 0B
setlocal enabledelayedexpansion

:: ==========================================
:: DETEKSI LOKASI JAVA (JDK/JRE)
:: ==========================================
echo Mencari instalasi Java di sistem Anda...

:: 1. Cek di System PATH
where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_EXE=java"
    where javac >nul 2>nul
    if %errorlevel% equ 0 (
        set "JAVAC_EXE=javac"
        set "JAR_EXE=jar"
    )
    goto :java_found
)

:: 2. Cek di JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
            set "JAR_EXE=%JAVA_HOME%\bin\jar.exe"
        )
        goto :java_found
    )
)

:: 3. Cek di folder Program Files (Oracle Java)
for /d %%d in ("C:\Program Files\Java\jdk*") do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_EXE=%%d\bin\java.exe"
        if exist "%%d\bin\javac.exe" (
            set "JAVAC_EXE=%%d\bin\javac.exe"
            set "JAR_EXE=%JAVA_HOME%\bin\jar.exe"
            goto :java_found
        )
    )
)

:: 4. Cek di folder Android Studio (JetBrains Runtime - JBR)
if exist "C:\Program Files\Android\Android Studio\jbr\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
    if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" (
        set "JAVAC_EXE=C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
        set "JAR_EXE=C:\Program Files\Android\Android Studio\jbr\bin\jar.exe"
        goto :java_found
    )
)

:: 5. Cek folder JRE standard jika hanya ingin menjalankan (tanpa kompilasi)
for /d %%d in ("C:\Program Files\Java\jre*") do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_EXE=%%d\bin\java.exe"
        goto :java_found
    )
)

:: Jika tidak ditemukan sama sekali
echo.
echo [PERINGATAN] Java tidak ditemukan di sistem Anda!
echo Aplikasi tidak dapat dijalankan tanpa Java Runtime Environment (JRE).
echo Kompilasi kode juga memerlukan Java Development Kit (JDK).
echo.
echo Silakan unduh dan instal Java JDK dari:
echo https://adoptium.net/ (Temurin OpenJDK)
echo atau pastikan JDK sudah diinstal dan dimasukkan ke variabel PATH Anda.
echo.
pause
exit /b 1

:java_found
echo Java terdeteksi di:
echo Runtime  : "%JAVA_EXE%"
if defined JAVAC_EXE (
    echo Compiler : "%JAVAC_EXE%"
) else (
    echo Compiler : Tidak ditemukan (Hanya bisa menjalankan, tidak bisa mengompilasi ulang)
)

:: ==========================================
:: DETEKSI MYSQL (XAMPP / LARAGON)
:: ==========================================
set "MYSQL_EXE="
set "DB_PANEL_EXE="
set "DB_PANEL_NAME="

:: Cek XAMPP
if exist "C:\xampp\mysql\bin\mysql.exe" (
    set "MYSQL_EXE=C:\xampp\mysql\bin\mysql.exe"
    set "DB_PANEL_NAME=XAMPP Control Panel"
    if exist "C:\xampp\xampp-control.exe" set "DB_PANEL_EXE=C:\xampp\xampp-control.exe"
) else (
    :: Cek Laragon
    for /d %%d in ("C:\laragon\bin\mysql\mysql-*") do (
        if exist "%%d\bin\mysql.exe" (
            set "MYSQL_EXE=%%d\bin\mysql.exe"
            set "DB_PANEL_NAME=Laragon"
            if exist "C:\laragon\laragon.exe" set "DB_PANEL_EXE=C:\laragon\laragon.exe"
        )
    )
)

if defined MYSQL_EXE (
    echo Database : Terdeteksi (%DB_PANEL_NAME% MySQL)
) else (
    echo Database : Tidak terdeteksi secara otomatis (Pastikan MySQL berjalan manual)
)
timeout /t 2 >nul

:: ==========================================
:: MENU UTAMA
:: ==========================================
:menu
cls
echo ============================================================
echo         APLIKASI PENGGAJIAN KARYAWAN (JAVA DESKTOP)
echo ============================================================
echo   [1] Jalankan Aplikasi (Run)
echo   [2] Compile & Jalankan Aplikasi (Build & Run)
echo   [3] Import Database ke MySQL
if defined DB_PANEL_EXE (
    echo   [4] Buka !DB_PANEL_NAME!
) else (
    echo   [4] Buka XAMPP/Laragon (Tidak Terdeteksi)
)
echo   [5] Keluar
echo ============================================================
set /p "pilihan=Pilih opsi (1-5): "

if "%pilihan%"=="1" goto :run_app
if "%pilihan%"=="2" goto :compile_app
if "%pilihan%"=="3" goto :import_db
if "%pilihan%"=="4" goto :open_panel
if "%pilihan%"=="5" exit /b 0

echo Opsi tidak valid! Silakan masukkan angka 1 sampai 5.
timeout /t 2 >nul
goto :menu

:: ==========================================
:: OPSI 1: JALANKAN APLIKASI
:: ==========================================
:run_app
cls
if not exist "dist\AplikasiPenggajian.jar" (
    echo [INFO] File executable JAR tidak ditemukan di dist\AplikasiPenggajian.jar
    echo Mencoba mengompilasi kode terlebih dahulu...
    timeout /t 2 >nul
    goto :compile_app
)

:run_jar
echo.
echo Menjalankan Aplikasi Penggajian Karyawan...
echo (Anda dapat menutup jendela ini setelah selesai menggunakan aplikasi)
echo.
cd /d "%~dp0"
"%JAVA_EXE%" -jar "dist\AplikasiPenggajian.jar"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Aplikasi berhenti dengan kode error %errorlevel%.
    echo Pastikan database MySQL sudah berjalan dan diimpor dengan benar.
    pause
)
goto :menu

:: ==========================================
:: OPSI 2: COMPILE & JALANKAN APLIKASI
:: ==========================================
:compile_app
cls
echo ============================================================
echo             PROSES KOMPILASI DAN MEMBUAT JAR
echo ============================================================
if not defined JAVAC_EXE (
    echo.
    echo [ERROR] Compiler 'javac' tidak ditemukan!
    echo Anda memerlukan JDK (Java Development Kit) untuk kompilasi.
    echo Pastikan Anda menginstal JDK atau gunakan opsi [1] jika file JAR sudah ada.
    pause
    goto :menu
)

:: Buat folder output jika belum ada
if not exist "build\classes" mkdir "build\classes"
if not exist "dist\lib" mkdir "dist\lib"

:: Copy library MySQL Connector dan JCalendar ke folder dist/lib
if exist "lib\mysql-connector-java-8.0.28.jar" (
    copy /y "lib\mysql-connector-java-8.0.28.jar" "dist\lib\" >nul
) else (
    echo [PERINGATAN] lib\mysql-connector-java-8.0.28.jar tidak ditemukan!
)
if exist "lib\jcalendar-1.4.jar" (
    copy /y "lib\jcalendar-1.4.jar" "dist\lib\" >nul
) else (
    echo [PERINGATAN] lib\jcalendar-1.4.jar tidak ditemukan!
)

echo 1. Mengompilasi source code (.java) ke build/classes...
"%JAVAC_EXE%" -d build\classes -cp "lib\mysql-connector-java-8.0.28.jar;lib\jcalendar-1.4.jar" src\penggajian\*.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Kompilasi gagal! Silakan periksa kembali kode program Anda.
    pause
    goto :menu
)

echo 2. Membuat file MANIFEST.MF...
echo Manifest-Version: 1.0 > manifest.tmp
echo Class-Path: lib/mysql-connector-java-8.0.28.jar lib/jcalendar-1.4.jar >> manifest.tmp
echo Main-Class: penggajian.Main >> manifest.tmp
echo. >> manifest.tmp

echo 3. Mengemas kelas-kelas ke dalam dist/AplikasiPenggajian.jar...
"%JAR_EXE%" cfm dist\AplikasiPenggajian.jar manifest.tmp -C build\classes .
del manifest.tmp

echo.
echo [SUKSES] Kompilasi berhasil!
echo File JAR dibuat di: dist\AplikasiPenggajian.jar
timeout /t 2 >nul
goto :run_jar

:: ==========================================
:: OPSI 3: IMPORT DATABASE KE MYSQL
:: ==========================================
:import_db
cls
echo ============================================================
echo                     IMPORT DATABASE KE MYSQL
echo ============================================================

:: Cek keberadaan file SQL
if not exist "sql\penggajian.sql" (
    echo [ERROR] File sql\penggajian.sql tidak ditemukan!
    pause
    goto :menu
)

if not defined MYSQL_EXE (
    echo [ERROR] Eksekutor MySQL tidak ditemukan secara otomatis.
    echo Silakan import file sql\penggajian.sql secara manual melalui phpMyAdmin.
    pause
    goto :menu
)

echo Menggunakan database dari: !DB_PANEL_NAME!

:: Cek apakah MySQL server berjalan
tasklist /FI "IMAGENAME eq mysqld.exe" 2>nul | find /I /N "mysqld.exe" >nul
if %errorlevel% neq 0 (
    echo MySQL Server terdeteksi MATI.
    echo Silakan aktifkan MySQL terlebih dahulu lewat !DB_PANEL_NAME!.
    if defined DB_PANEL_EXE (
        echo Membuka !DB_PANEL_NAME! untuk Anda...
        start "" "!DB_PANEL_EXE!"
    )
    echo.
    echo Tekan tombol apa saja setelah MySQL menyala untuk melanjutkan import...
    pause >nul
) else (
    echo MySQL Server terdeteksi AKTIF.
)

echo Mengimpor sql\penggajian.sql ke MySQL...
"!MYSQL_EXE!" -u root --force < "sql\penggajian.sql"
if %errorlevel% equ 0 (
    echo.
    echo [SUKSES] Database 'db_fardhan' berhasil diimpor!
    echo Username default login: admin
    echo Password default login: admin
) else (
    echo.
    echo [ERROR] Gagal mengimpor database.
    echo Pastikan MySQL sudah di-start di panel database Anda.
)
pause
goto :menu

:: ==========================================
:: OPSI 4: BUKA PANEL DATABASE
:: ==========================================
:open_panel
cls
if defined DB_PANEL_EXE (
    echo Membuka !DB_PANEL_NAME!...
    start "" "!DB_PANEL_EXE!"
) else (
    echo [ERROR] Panel database tidak terdeteksi secara otomatis.
    echo Silakan jalankan XAMPP Control Panel atau Laragon secara manual.
)
timeout /t 2 >nul
goto :menu
