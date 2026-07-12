# مستند طراحی سامانه ثبت آگهی خرید و فروش دست دوم

این سند خلاصه تحلیل نیازمندی‌ها، طراحی معماری، طراحی پایگاه داده، طراحی REST API، طراحی JavaFX و نقشه راه پیاده‌سازی پروژه است. این سند پیش از شروع کدنویسی تهیه شده و مرجع اصلی برای پیاده‌سازی است.

منابع: مستندات دانشگاهی `AP_project_guide.pdf` و `AP_project_final.pdf` (اولویت اول - الزامات دانشگاه) و `MasterPrompt2.md` (استراتژی پیاده‌سازی، در مواردی که با مستندات دانشگاه تضاد نداشته باشد).

## 1. تحلیل نیازمندی‌ها (خلاصه)

نقش‌ها: کاربر عادی (USER)، مدیر سیستم (ADMIN).

قابلیت‌های کاربر عادی: ثبت‌نام/ورود/خروج، مشاهده و جست‌وجو و فیلتر آگهی‌های فعال، مشاهده جزئیات آگهی، ثبت/ویرایش/حذف آگهی خودش، تغییر وضعیت آگهی به فروخته‌شده، افزودن/حذف/مشاهده علاقه‌مندی‌ها، شروع گفت‌وگو با فروشنده، ارسال/مشاهده پیام، مشاهده لیست گفت‌وگوها، امتیازدهی به فروشنده.

قابلیت‌های مدیر: ورود به پنل مدیریت، مشاهده آگهی‌های در انتظار بررسی، تایید/رد آگهی (با دلیل رد)، حذف آگهی نامناسب، مدیریت دسته‌بندی‌ها، مشاهده لیست کاربران، مسدود/فعال‌سازی مجدد کاربر.

قوانین کلیدی: هر آگهی جدید با وضعیت PENDING ذخیره می‌شود و تنها پس از تایید مدیر ACTIVE و در نتیجه عمومی می‌شود. فقط آگهی‌های ACTIVE در جست‌وجوی عمومی نمایش داده می‌شوند. فقط مالک آگهی می‌تواند آن را ویرایش/حذف/فروخته‌شده کند. کاربر مسدود نمی‌تواند وارد شود/آگهی ثبت کند/پیام بدهد/امتیاز بدهد/علاقه‌مندی اضافه کند. کاربر نمی‌تواند به آگهی خودش پیام بدهد یا امتیاز بدهد. هر خریدار برای هر آگهی فقط یک‌بار می‌تواند امتیاز بدهد (بین ۱ تا ۵). هر کاربر یک آگهی را فقط یک‌بار می‌تواند علاقه‌مندی کند. برای هر (خریدار، فروشنده، آگهی) فقط یک گفت‌وگو وجود دارد.

## 2. معماری کلی

معماری کلاینت-سرور سه‌لایه:

```
JavaFX Frontend  --(HTTP/JSON, REST)-->  Spring Boot Backend  --(JPA/Hibernate)-->  PostgreSQL
```

Frontend هرگز مستقیم به دیتابیس متصل نمی‌شود و تمام منطق تجاری، احراز هویت و اعتبارسنجی نهایی در Backend انجام می‌شود.

Backend لایه‌ای: `controller -> service -> repository -> entity`. کنترلرها فقط HTTP را مدیریت می‌کنند، منطق در Service است، Repository فقط با پایگاه داده کار می‌کند.

Frontend لایه‌ای: `fxml/controller -> service -> client(ApiClient) -> backend`.

## 3. مدل دامنه و ER Diagram

موجودیت‌ها:

- **User**: id, fullName, username(unique), password(hash), phoneNumber(unique), email(unique، اختیاری), role[USER,ADMIN], status[ACTIVE,BLOCKED], createdAt, updatedAt
- **Category**: id, title, description, parent(Category، nullable، خودارجاع برای زیردسته)
- **City**: id, name(unique)
- **Advertisement**: id, title, description, price, status[PENDING,ACTIVE,REJECTED,SOLD,DELETED], rejectionReason, owner(User), category(Category), city(City), createdAt, updatedAt
- **AdvertisementImage**: id, imagePath, displayOrder, advertisement(Advertisement)
- **Favorite**: id, user(User), advertisement(Advertisement), createdAt — یکتا بر (user, advertisement)
- **Conversation**: id, buyer(User), seller(User), advertisement(Advertisement), createdAt — یکتا بر (buyer, seller, advertisement)
- **ChatMessage**: id, conversation(Conversation), sender(User), content, createdAt
- **Rating**: id, buyer(User), seller(User), advertisement(Advertisement), score[1..5], comment, createdAt — یکتا بر (buyer, advertisement)

ارتباطات:

```
User (1) ----owns----> (*) Advertisement
Advertisement (1) ----has----> (*) AdvertisementImage
Category (1) ----categorizes----> (*) Advertisement
Category (1) ----parent of----> (*) Category
City (1) ----located in----> (*) Advertisement
User (1) ----saves----> (*) Favorite <----saved from---- (1) Advertisement
User(buyer) (1) --> (*) Conversation ; User(seller) (1) --> (*) Conversation ; Advertisement (1) --> (*) Conversation
Conversation (1) ----> (*) ChatMessage <---- (1) User(sender)
User(buyer) (1) --> (*) Rating ; User(seller) (1) --> (*) Rating ; Advertisement (1) --> (*) Rating
```

استراتژی کلید: `Long` با `@GeneratedValue(strategy = IDENTITY)` برای همه موجودیت‌ها. بارگذاری روابط `LAZY` مگر در موارد ضروری.

## 4. طراحی REST API

پیشوند پایه: `/api`

پاسخ استاندارد (`ApiResponse<T>`): `{ success, message, data, timestamp }`

### Auth
- `POST /api/auth/register` – ثبت‌نام
- `POST /api/auth/login` – ورود، بازگرداندن JWT
- `GET /api/auth/me` – کاربر جاری (نیاز به JWT)

### Categories
- `GET /api/categories` – لیست دسته‌بندی‌ها (عمومی)
- `POST /api/categories` – ایجاد (ADMIN)
- `PUT /api/categories/{id}` – ویرایش (ADMIN)
- `DELETE /api/categories/{id}` – حذف (ADMIN)

### Cities
- `GET /api/cities` – لیست شهرها (عمومی)

### Advertisements
- `GET /api/advertisements` – جست‌وجو/فیلتر/مرتب‌سازی آگهی‌های فعال (پارامترها: keyword, categoryId, cityId, minPrice, maxPrice, sort)
- `GET /api/advertisements/{id}` – جزئیات آگهی
- `GET /api/advertisements/my` – آگهی‌های کاربر جاری (نیاز به JWT)
- `POST /api/advertisements` – ثبت آگهی جدید (نیاز به JWT)
- `PUT /api/advertisements/{id}` – ویرایش (فقط مالک)
- `DELETE /api/advertisements/{id}` – حذف منطقی (مالک یا ADMIN)
- `PATCH /api/advertisements/{id}/sold` – تغییر وضعیت به فروخته‌شده (فقط مالک)
- `POST /api/advertisements/{id}/images` – افزودن تصویر (فقط مالک، multipart)

### Favorites
- `GET /api/favorites` – لیست علاقه‌مندی‌های کاربر
- `POST /api/favorites/{advertisementId}` – افزودن
- `DELETE /api/favorites/{advertisementId}` – حذف

### Conversations & Messages
- `GET /api/conversations` – لیست گفت‌وگوهای کاربر
- `POST /api/conversations` – ایجاد یا بازیابی گفت‌وگو برای یک آگهی
- `GET /api/conversations/{id}/messages` – پیام‌های یک گفت‌وگو
- `POST /api/conversations/{id}/messages` – ارسال پیام جدید

### Ratings
- `POST /api/ratings` – ثبت امتیاز برای فروشنده یک آگهی
- `GET /api/ratings/seller/{sellerId}` – لیست و میانگین امتیازهای فروشنده

### Admin
- `GET /api/admin/advertisements/pending` – آگهی‌های در انتظار بررسی
- `PUT /api/admin/advertisements/{id}/approve`
- `PUT /api/admin/advertisements/{id}/reject` (body: reason)
- `DELETE /api/admin/advertisements/{id}`
- `GET /api/admin/users`
- `PUT /api/admin/users/{id}/block`
- `PUT /api/admin/users/{id}/unblock`
- `GET /api/admin/stats` – آمار کلی (تعداد کاربران، آگهی‌ها، در انتظار بررسی)

کدهای وضعیت: 200، 201، 204، 400، 401، 403، 404، 409، 500 طبق نیاز.

## 5. طراحی امنیت

JWT شامل username، role و زمان انقضا. فیلتر `JwtAuthenticationFilter` روی هر درخواست، توکن را استخراج و کاربر را در `SecurityContext` قرار می‌دهد. مسیرهای عمومی: `/api/auth/**`, `GET /api/advertisements/**` (فقط فعال)، `/api/categories`, `/api/cities`, `GET /api/ratings/**`. باقی مسیرها نیاز به JWT دارند و مسیرهای `/api/admin/**` فقط برای نقش ADMIN مجاز است. مالکیت (owner) همیشه از JWT استخراج می‌شود، نه از بدنه درخواست.

## 6. ساختار پکیج Backend

```
backend/src/main/java/ir/secondhand/backend/
  SecondHandBackendApplication.java
  config/        SecurityConfig, CorsConfig, DataSeeder, WebConfig(static files)
  security/      JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
  controller/    AuthController, AdvertisementController, CategoryController, CityController,
                 FavoriteController, ConversationController, RatingController, AdminController
  service/       AuthService, AdvertisementService, CategoryService, CityService, FavoriteService,
                 ConversationService, RatingService, AdminService, ImageStorageService
  repository/    UserRepository, AdvertisementRepository, AdvertisementImageRepository,
                 CategoryRepository, CityRepository, FavoriteRepository, ConversationRepository,
                 ChatMessageRepository, RatingRepository
  entity/        User, Advertisement, AdvertisementImage, Category, City, Favorite, Conversation,
                 ChatMessage, Rating, enums(Role, UserStatus, AdvertisementStatus)
  dto/request/   RegisterRequest, LoginRequest, AdvertisementRequest, AdvertisementRejectRequest,
                 MessageRequest, RatingRequest, CategoryRequest
  dto/response/  ApiResponse, AuthResponse, UserResponse, AdvertisementResponse,
                 AdvertisementSummaryResponse, CategoryResponse, CityResponse,
                 ConversationResponse, ChatMessageResponse, RatingResponse, AdminStatsResponse
  exception/     GlobalExceptionHandler, ResourceNotFoundException, DuplicateResourceException,
                 ForbiddenOperationException
```

## 7. ساختار پکیج Frontend

```
frontend/src/main/java/ir/secondhand/frontend/
  MainApplication.java, Launcher.java (نقطه ورود بدون وابستگی به module-path)
  config/       ApiConfig
  client/       ApiClient, ApiException
  session/      SessionManager
  dto/request/  (نگاشت درخواست‌های backend برای Jackson)
  dto/response/ (نگاشت پاسخ‌های backend برای Jackson)
  service/      AuthService, AdvertisementService, CategoryService, CityService, FavoriteService,
                ConversationService, RatingService, AdminService
  controller/   LoginController, RegisterController, HomeController, AdDetailController,
                AdFormController (ثبت و ویرایش با یک فرم مشترک)، MyAdsController،
                FavoritesController, ConversationsController, ConversationDetailController,
                AdminDashboardController (شامل تب‌های آمار، آگهی در انتظار، کاربران، دسته‌بندی، شهر)
  controller/component/ NavBarController (نوار ناوبری مشترک بین تمام صفحات)
  util/         AlertHelper, Navigator, AdCardFactory, StatusLabelFactory, PriceFormatter
resources/
  fxml/                *.fxml برای هر صفحه
  fxml/components/     navbar.fxml
  css/style.css
```

## 8. نقشه راه پیاده‌سازی (فازها)

1. راه‌اندازی اولیه پروژه (ساختار مخزن، Maven backend/frontend، Docker، gitignore)
2. تنظیمات دیتابیس و اتصال Spring Boot به PostgreSQL
3. مدل دامنه (Entity ها) و Repository ها
4. احراز هویت (ثبت‌نام، ورود، JWT، Security Config، نقش‌ها)
5. ماژول آگهی (CRUD، تصاویر، وضعیت‌ها، مالکیت)
6. جست‌وجو و فیلتر و مرتب‌سازی
7. علاقه‌مندی‌ها
8. گفت‌وگو و پیام‌ها
9. امتیازدهی
10. پنل مدیریت
11. اسکلت JavaFX (Navigator، SessionManager، ApiClient)
12. اتصال کامل صفحات JavaFX به Backend
13. داده‌های نمونه (Seed) و تست نهایی و نگارش README

هر فاز باید پیش از رفتن به فاز بعد بدون خطای Build باشد.

## 9. بازبینی طراحی (Self Review)

- موجودیت‌ها بدون تکرار و بدون وابستگی چرخه‌ای هستند.
- هر رابطه دارای کلید خارجی مشخص است (owner, category, city, buyer, seller, advertisement, conversation, sender, parent).
- تمام قوانین کسب‌وکار (وضعیت PENDING پیش‌فرض، مالکیت، جلوگیری از تکرار Favorite/Rating/Conversation) در Service قابل پیاده‌سازی و تست است.
- طراحی REST API از اسم به‌جای فعل استفاده می‌کند و متدهای HTTP درست انتخاب شده‌اند.
- معماری با محدودیت‌های سطح دانشجویی (بدون میکروسرویس، بدون Kafka/Redis/CQRS) مطابقت دارد.

طراحی تایید شد؛ پیاده‌سازی از فاز ۱ آغاز می‌شود.
