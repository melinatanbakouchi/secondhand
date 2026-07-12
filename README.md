# بازار دست دوم (SecondHandMarket)

سامانه دسکتاپ ثبت‌آگهی خرید و فروش کالای دست دوم؛ متشکل از یک Frontend با JavaFX، یک Backend با Spring Boot و پایگاه‌داده PostgreSQL. ارتباط Frontend و Backend صرفا از طریق REST API (JSON روی HTTP) انجام می‌شود و Frontend هیچ‌گاه مستقیم به دیتابیس متصل نمی‌شود.

مستند کامل تحلیل نیازمندی‌ها، طراحی معماری، مدل دامنه، REST API و نقشه راه پیاده‌سازی در [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) موجود است.

## پشته فناوری

| لایه | فناوری |
|---|---|
| Frontend | Java 17، JavaFX 21، FXML، CSS، `java.net.http.HttpClient`، Jackson |
| Backend | Java 17، Spring Boot 3.2، Spring Web، Spring Data JPA (Hibernate)، Spring Security، JWT (jjwt)، Lombok |
| پایگاه‌داده | PostgreSQL 16 |
| ابزار build | Maven (ماژول‌های مجزای `backend` و `frontend`) |

## ساختار مخزن

```
SecondHandMarket/
  backend/     سرویس Spring Boot (REST API)
  frontend/    کلاینت دسکتاپ JavaFX
  docs/        مستندات طراحی و تحلیل
  docker-compose.yml
  README.md
```

## پیش‌نیازها

- JDK 17 یا بالاتر
- Maven 3.9+
- PostgreSQL 16 در دسترس (به‌صورت محلی یا از طریق Docker)
- (اختیاری) Docker و Docker Compose برای اجرای سریع پایگاه‌داده/Backend

## راه‌اندازی پایگاه‌داده

### روش ۱: Docker (پیشنهادی)

```bash
docker compose up -d postgres
```

این دستور یک نمونه PostgreSQL با پایگاه‌داده `secondhand_db`، کاربر `secondhand_user` و رمز `secondhand_password` روی پورت `5432` بالا می‌آورد (مطابق تنظیمات `backend/src/main/resources/application.properties`).

### روش ۲: PostgreSQL محلی

پایگاه‌داده و کاربری با مقادیر زیر بسازید (یا `backend/src/main/resources/application.properties` را با مقادیر خودتان به‌روز کنید):

```sql
CREATE DATABASE secondhand_db;
CREATE USER secondhand_user WITH ENCRYPTED PASSWORD 'secondhand_password';
GRANT ALL PRIVILEGES ON DATABASE secondhand_db TO secondhand_user;
```

جدول‌ها به‌صورت خودکار توسط Hibernate (`spring.jpa.hibernate.ddl-auto=update`) ساخته می‌شوند و در اولین اجرا داده‌های نمونه (کاربران، دسته‌بندی‌ها، شهرها، آگهی‌ها، علاقه‌مندی‌ها، گفت‌وگو و امتیاز) به‌صورت خودکار درج می‌شود (`app.seed.enabled=true`).

## اجرای Backend

```bash
cd backend
mvn spring-boot:run
```

سرویس روی آدرس `http://localhost:8080` بالا می‌آید. مسیر پایه API: `http://localhost:8080/api`.

برای build و اجرای jar نهایی:

```bash
cd backend
mvn clean package -DskipTests
java -jar target/secondhand-backend.jar
```

## اجرای Frontend

```bash
cd frontend
mvn javafx:run
```

Frontend به‌صورت پیش‌فرض به `http://localhost:8080/api` وصل می‌شود (`ir.secondhand.frontend.config.ApiConfig`). پیش از اجرای Frontend، Backend باید در حال اجرا باشد.

برای ساخت jar نهایی قابل اجرا (بدون نیاز به `mvn javafx:run`):

```bash
cd frontend
mvn clean package
java -jar target/secondhand-frontend.jar
```

## اجرای کامل با Docker Compose

```bash
docker compose up --build
```

این دستور PostgreSQL و Backend را در کنار هم بالا می‌آورد. سپس Frontend را طبق دستور بالا به‌صورت جدا روی سیستم خود اجرا کنید (Frontend دسکتاپ است و در کانتینر اجرا نمی‌شود).

## کاربران نمونه (Seed Data)

| نقش | نام کاربری | رمز عبور |
|---|---|---|
| مدیر سیستم | `admin` | `Admin@123` |
| کاربر عادی (فروشنده) | `ali.rezaei` | `User@123` |
| کاربر عادی (فروشنده) | `maryam.ahmadi` | `User@123` |
| کاربر عادی (خریدار) | `hossein.karimi` | `User@123` |
| کاربر عادی (خریدار) | `zahra.mousavi` | `User@123` |

## قابلیت‌های اصلی

- ثبت‌نام، ورود و احراز هویت مبتنی بر JWT با نقش‌های کاربر عادی و مدیر سیستم.
- ثبت، ویرایش، حذف منطقی و تغییر وضعیت آگهی (در انتظار بررسی، فعال، رد شده، فروخته‌شده، حذف‌شده) با آپلود تصویر.
- جست‌وجو و فیلتر آگهی‌های فعال بر اساس عنوان، دسته‌بندی، شهر و بازه قیمت.
- افزودن/حذف آگهی از علاقه‌مندی‌ها.
- گفت‌وگوی متنی بین خریدار و فروشنده درباره یک آگهی مشخص.
- امتیازدهی خریدار به فروشنده پس از تعامل درباره یک آگهی.
- پنل مدیریت: تایید/رد آگهی (با دلیل رد)، مدیریت کاربران (مسدود/رفع مسدودیت)، مدیریت دسته‌بندی‌ها و شهرها، آمار کلی سامانه.
- تمام رابط کاربری به زبان فارسی و با چیدمان راست‌به‌چپ (RTL) و طراحی الهام‌گرفته از دیوار (پس‌زمینه سفید، دکمه‌های سبز، کارت‌های گردگوشه).

## نکات امنیتی و معماری

- Frontend هرگز مستقیم به پایگاه‌داده وصل نمی‌شود؛ همه عملیات از طریق REST API انجام می‌شود.
- اعتبارسنجی نهایی و منطق تجاری همیشه در Backend انجام می‌گیرد (اعتبارسنجی در Frontend صرفا برای تجربه کاربری بهتر است).
- مالکیت منابع (مثلا مالک آگهی) همیشه از روی JWT استخراج می‌شود، نه از بدنه درخواست.
- خطاها به‌صورت متمرکز در `GlobalExceptionHandler` مدیریت شده و پاسخ JSON استاندارد با پیام فارسی بازمی‌گردانند؛ هیچ Stack Trace به کاربر نمایش داده نمی‌شود.
