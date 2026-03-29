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

-- Bảng sequences dùng để sinh ID tập trung
CREATE TABLE IF NOT EXISTS sequences (
    name     VARCHAR(50) PRIMARY KEY,
    next_val BIGINT      NOT NULL DEFAULT 1
);

INSERT IGNORE INTO sequences (name, next_val) VALUES
    ('HOADON',     1),
    ('KHUYENMAI',  1),
    ('KHACHHANG',  1),
    ('SANPHAM',    1),
    ('NHACUNGCAP', 1);

-- Dữ liệu mẫu (Tùy chọn)
INSERT IGNORE INTO nhacungcap (mancc, tenncc, diachi, sodienthoai) VALUES
    ('NCC_TEST',   'Công ty Phân Phối Việt', 'Cần Thơ',     '0123456789'),
    ('NCC_APPLE',  'Apple Việt Nam',          'Hồ Chí Minh', '0281234567'),
    ('NCC_SAMSUNG','Samsung Electronics VN',  'Hà Nội',      '0241112222'),
    ('NCC_LOCAL',  'Nhà Cung Cấp Nội Địa',   'Đà Nẵng',     '0236999888');

INSERT IGNORE INTO sanpham (masp, tensp, gia, soluongton, mancc) VALUES
    ('SP2JOH1B', 'Laptop Dell XPS 13',    22000000, 200,  'NCC_APPLE'),
    ('SP_IP15',  'iPhone 15 Pro',         28500000, 50,   'NCC_APPLE'),
    ('SP_S24',   'Samsung Galaxy S24',    20000000, 80,   'NCC_SAMSUNG'),
    ('SP_TAB',   'Samsung Galaxy Tab S9', 15000000, 30,   'NCC_SAMSUNG'),
    ('SP_MOUSE', 'Chuột không dây Logitech', 350000, 150, 'NCC_LOCAL'),
    ('SP_KB',    'Bàn phím cơ Keychron',   1200000,  60,  'NCC_LOCAL'),
    ('SP_MON',   'Màn hình LG 27" 4K',    8500000,  25,  'NCC_TEST'),
    ('SP_HD',    'Ổ cứng SSD 1TB Samsung', 2200000,  90,  'NCC_SAMSUNG');

INSERT IGNORE INTO khachhang (makh, hoten, diachi, cccd) VALUES
    ('KH7INKSP', 'Nguyen Dang Khoa', 'Can tho',          '097123113111'),
    ('KH_ANH',   'Trần Thị Anh',    '12 Lê Lợi, HCM',   '079201234567'),
    ('KH_BINH',  'Lê Văn Bình',     '45 Hùng Vương, HN', '001302345678'),
    ('KH_CHI',   'Phạm Thị Chi',    '8 Trần Phú, ĐN',   '048103456789'),
    ('KH_DUNG',  'Nguyễn Văn Dũng', '22 Nguyễn Huệ, CT', '092004567890');

INSERT IGNORE INTO khuyenmai (makm, ngayapdung, ngayketthuc, phantramgiam) VALUES
    ('KM-202603-0001', '2026-01-01', '2026-12-31', 0.1),
    ('KM_TET26',       '2026-01-15', '2026-02-28', 0.15),
    ('KM_SUMMER',      '2026-06-01', '2026-08-31', 0.05);

