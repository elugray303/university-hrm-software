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


CREATE TABLE NCKH (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    MaNV VARCHAR(20),
    TenDeTai NVARCHAR(200),
    LoaiHinh NVARCHAR(50),
    NgayCongBo DATE,
    DiemThuong FLOAT,
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

-- Thêm vài dữ liệu mẫu
INSERT INTO NCKH (MaNV, TenDeTai, LoaiHinh, NgayCongBo, DiemThuong) 
VALUES ('GV02', N'Ứng dụng AI trong giáo dục', 'Scopus', '2025-05-15', 2.0);

CREATE TABLE QuaTrinhCongTac (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    MaNV VARCHAR(20),
    ThoiGian NVARCHAR(50), -- Ví dụ: 2010 - 2014
    DonVi NVARCHAR(200),   -- Ví dụ: ĐH Bách Khoa Hà Nội
    ChucVu NVARCHAR(100),  -- Ví dụ: Sinh viên / Giảng viên
    GhiChu NVARCHAR(200),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);


CREATE TABLE XepLoai (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    MaNV VARCHAR(20),
    NamHoc INT,
    TongTietDay INT,
    TongDiemNCKH FLOAT,
    XepLoai NVARCHAR(50), -- Ví dụ: Hoàn thành xuất sắc, Hoàn thành tốt...
    GhiChu NVARCHAR(200),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

-- 1. Thêm cột Quyen và MaNV
ALTER TABLE TaiKhoan ADD Quyen NVARCHAR(50); -- Ví dụ: 'Admin' hoặc 'GiangVien'
ALTER TABLE TaiKhoan ADD MaNV VARCHAR(20);   -- Liên kết với bảng NhanVien

-- 2. Cập nhật dữ liệu mẫu (Quan trọng)
-- Tài khoản admin (Quyền Admin)
UPDATE TaiKhoan SET Quyen = 'Admin', MaNV = 'NV01' WHERE TenDangNhap = 'admin';

-- Tạo thêm tài khoản giảng viên để test (Quyền GiangVien)
INSERT INTO TaiKhoan (TenDangNhap, MatKhau, Quyen, MaNV) VALUES ('gv01', '123', 'GiangVien', 'GV02');