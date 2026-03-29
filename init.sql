CREATE DATABASE IF NOT EXISTS qlbh;
USE qlbh;

CREATE TABLE IF NOT EXISTS khachhang (
    makh VARCHAR(20) PRIMARY KEY,
    hoten VARCHAR(100),
    diachi VARCHAR(200),
    cccd VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS nhacungcap (
    mancc VARCHAR(20) PRIMARY KEY,
    tenncc VARCHAR(100),
    diachi VARCHAR(200),
    sodienthoai VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS sanpham (
    masp VARCHAR(20) PRIMARY KEY,
    tensp VARCHAR(100),
    gia DOUBLE,
    soluongton INT,
    mancc VARCHAR(20),
    FOREIGN KEY (mancc) REFERENCES nhacungcap(mancc)
);

CREATE TABLE IF NOT EXISTS khuyenmai (
    makm VARCHAR(20) PRIMARY KEY,
    ngayapdung DATE,
    ngayketthuc DATE,
    phantramgiam DOUBLE
);

CREATE TABLE IF NOT EXISTS hoadon (
    mahd VARCHAR(20) PRIMARY KEY,
    ngaylap DATE,
    vat DOUBLE,
    makh VARCHAR(20),
    makm VARCHAR(20),
    FOREIGN KEY (makh) REFERENCES khachhang(makh),
    FOREIGN KEY (makm) REFERENCES khuyenmai(makm)
);

CREATE TABLE IF NOT EXISTS chitiethd (
    mahd VARCHAR(20),
    masp VARCHAR(20),
    soluong INT,
    dongia DOUBLE,
    PRIMARY KEY (mahd, masp),
    FOREIGN KEY (mahd) REFERENCES hoadon(mahd),
    FOREIGN KEY (masp) REFERENCES sanpham(masp)
);

-- Dữ liệu mẫu (Tùy chọn)
INSERT IGNORE INTO nhacungcap (mancc, tenncc, diachi, sodienthoai) VALUES ('NCC_TEST', 'Công ty Phân Phối Việt', 'Cần Thơ', '0123456789');
