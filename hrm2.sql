create database hrm2
use hrm2


-- 1. tai khoan
CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(50) NOT NULL,
    VaiTro NVARCHAR(50) DEFAULT 'Admin'
);

-- 2. khoa
CREATE TABLE Khoa (
    MaKhoa VARCHAR(10) PRIMARY KEY, 
    TenKhoa NVARCHAR(100) NOT NULL
);

-- 3. can bo
CREATE TABLE NhanVien (
    MaNV VARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE,
    GioiTinh NVARCHAR(10) CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    MaKhoa VARCHAR(10),
    ChucVu NVARCHAR(50),           
    TrinhDo NVARCHAR(50),
    LoaiHinh NVARCHAR(50),
    TrangThai NVARCHAR(50),        
    HeSoLuong FLOAT DEFAULT 1.0,
    LuongCoBan FLOAT DEFAULT 0,
    PhuCap FLOAT DEFAULT 0,
    HinhAnh VARCHAR(200),          
    FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
);

-- 4. mon hoc
CREATE TABLE MonHoc (
    MaMon VARCHAR(20) PRIMARY KEY, 
    TenMon NVARCHAR(100) NOT NULL,
    SoTinChi INT NOT NULL,
    MaKhoa VARCHAR(10),            -- Môn này thuộc Khoa nào
    FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
);

-- 5. tkb
CREATE TABLE LichDay (
    MaLich INT IDENTITY(1,1) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL,
    TenMonHoc NVARCHAR(100),
    PhongHoc VARCHAR(20),
    Thu INT,                        
    TietBatDau INT,                 
    SoTiet INT,
    TuNgay DATE,                    
    DenNgay DATE,                   
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);


INSERT INTO TaiKhoan (TenDangNhap, MatKhau) VALUES ('admin', '123456');


-- 6. luong
CREATE TABLE BangLuong (
    MaBL INT IDENTITY(1,1) PRIMARY KEY, -- Mã tự tăng
    MaNV VARCHAR(20) NOT NULL,
    Thang INT NOT NULL,
    Nam INT NOT NULL,
    LuongCung DECIMAL(18,0), -- Lương cứng đã chốt
    TongTiet INT,            -- Tổng số tiết đã dạy trong tháng đó
    ThuLao DECIMAL(18,0),    -- Thù lao dạy đã chốt
    ThucLinh DECIMAL(18,0),  -- Thực lĩnh cuối cùng
    NgayChot DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

ALTER TABLE BangLuong ADD LuongCB FLOAT DEFAULT 0;
ALTER TABLE BangLuong ADD HeSo FLOAT DEFAULT 1;
ALTER TABLE BangLuong ADD PhuCap FLOAT DEFAULT 0;