package ir.secondhand.backend.seed;

import ir.secondhand.backend.entity.*;
import ir.secondhand.backend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * در نخستین اجرای برنامه (وقتی پایگاه‌داده خالی است) داده‌های نمونه برای همه
 * موجودیت‌ها ایجاد می‌شود تا سامانه از همان ابتدا قابل نمایش و تست باشد.
 * این عملیات فقط زمانی اجرا می‌شود که app.seed.enabled=true و جدول کاربران خالی باشد.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final AdvertisementRepository advertisementRepository;
    private final FavoriteRepository favoriteRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RatingRepository ratingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    public DataSeeder(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       CityRepository cityRepository,
                       AdvertisementRepository advertisementRepository,
                       FavoriteRepository favoriteRepository,
                       ConversationRepository conversationRepository,
                       ChatMessageRepository chatMessageRepository,
                       RatingRepository ratingRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.advertisementRepository = advertisementRepository;
        this.favoriteRepository = favoriteRepository;
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.ratingRepository = ratingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) {
            return;
        }

        User admin = createUser("مدیر سامانه", "admin", "Admin@123", "09120000000", "admin@secondhand.ir", Role.ADMIN);
        User seller1 = createUser("علی رضایی", "ali.rezaei", "User@123", "09121111111", "ali@example.com", Role.USER);
        User seller2 = createUser("مریم احمدی", "maryam.ahmadi", "User@123", "09122222222", "maryam@example.com", Role.USER);
        User buyer1 = createUser("حسین کریمی", "hossein.karimi", "User@123", "09123333333", "hossein@example.com", Role.USER);
        User buyer2 = createUser("زهرا موسوی", "zahra.mousavi", "User@123", "09124444444", "zahra@example.com", Role.USER);

        Category mobileTablet = createCategory("موبایل و تبلت", "خرید و فروش گوشی موبایل و تبلت", null);
        createCategory("موبایل", "انواع گوشی موبایل", mobileTablet);
        createCategory("تبلت", "انواع تبلت", mobileTablet);
        Category vehicle = createCategory("وسایل نقلیه", "خودرو، موتورسیکلت و سایر وسایل نقلیه", null);
        createCategory("خودرو", "انواع خودرو", vehicle);
        createCategory("موتورسیکلت", "انواع موتورسیکلت", vehicle);
        Category homeAppliance = createCategory("لوازم خانگی", "یخچال، ماشین لباسشویی و سایر لوازم خانگی", null);
        Category furniture = createCategory("مبلمان و دکوراسیون", "مبل، میز و دکوراسیون منزل", null);
        Category digital = createCategory("لوازم دیجیتال", "لپ‌تاپ، دوربین و لوازم جانبی", null);
        Category booksAndEducation = createCategory("کتاب و لوازم تحصیلی", "کتاب، دفتر و لوازم تحصیلی", null);
        Category clothing = createCategory("لباس و پوشاک", "لباس و اکسسوری", null);
        Category sportsAndLeisure = createCategory("ورزش و سرگرمی", "لوازم ورزشی و سرگرمی", null);

        City tehran = createCity("تهران");
        City mashhad = createCity("مشهد");
        City isfahan = createCity("اصفهان");
        City shiraz = createCity("شیراز");
        City tabriz = createCity("تبریز");
        City karaj = createCity("کرج");

        Advertisement ad1 = createAdvertisement(seller1, mobileTablet, tehran,
                "گوشی سامسونگ گلکسی S21 در حد نو",
                "گوشی سامسونگ گلکسی S21 با رنگ مشکی، ۱۲۸ گیگابایت حافظه داخلی، به همراه جعبه و شارژر اصلی. بدون خط و خش.",
                new BigDecimal("18500000"), AdvertisementStatus.ACTIVE);

        Advertisement ad2 = createAdvertisement(seller1, digital, tehran,
                "لپ‌تاپ ایسوس مناسب برنامه‌نویسی و طراحی",
                "لپ‌تاپ ایسوس با پردازنده i7 نسل ۱۱، رم ۱۶ گیگابایت و هارد SSD یک ترابایت. کارکرد بسیار کم.",
                new BigDecimal("42000000"), AdvertisementStatus.ACTIVE);

        Advertisement ad3 = createAdvertisement(seller2, furniture, isfahan,
                "مبل راحتی هفت‌نفره تمام‌چرم",
                "مبل راحتی هفت‌نفره، رویه چرم مصنوعی درجه یک، رنگ قهوه‌ای، استفاده کم و بدون پارگی.",
                new BigDecimal("35000000"), AdvertisementStatus.ACTIVE);

        Advertisement ad4 = createAdvertisement(seller2, homeAppliance, shiraz,
                "یخچال ساید بای ساید ال جی",
                "یخچال ساید بای ساید ال جی، دو درب، رنگ نقره‌ای، دارای یخ‌ساز و آب‌سردکن، گارانتی تا شش ماه دیگر معتبر است.",
                new BigDecimal("28000000"), AdvertisementStatus.SOLD);

        createAdvertisement(seller1, clothing, tehran,
                "کاپشن چرم مردانه سایز XL",
                "کاپشن چرم طبیعی، سایز XL، رنگ مشکی، پوشیده‌شده در حد نو.",
                new BigDecimal("3200000"), AdvertisementStatus.PENDING);

        Advertisement rejectedAd = createAdvertisement(seller2, sportsAndLeisure, tabriz,
                "دوچرخه کوهستان حرفه‌ای",
                "دوچرخه کوهستان با فریم آلومینیومی و دنده شیمانو، مناسب مسیرهای کوهستانی.",
                new BigDecimal("9500000"), AdvertisementStatus.PENDING);
        rejectedAd.setStatus(AdvertisementStatus.REJECTED);
        rejectedAd.setRejectionReason("توضیحات آگهی ناقص است. لطفا جزئیات بیشتری از وضعیت کالا ارائه دهید.");
        advertisementRepository.save(rejectedAd);

        createAdvertisement(buyer1, booksAndEducation, mashhad,
                "مجموعه کامل کتاب‌های کنکور ریاضی",
                "مجموعه کامل کتاب‌های کمک‌آموزشی کنکور ریاضی، شامل جمع‌بندی و تست، بدون یادداشت‌نویسی.",
                new BigDecimal("1200000"), AdvertisementStatus.ACTIVE);

        createAdvertisement(buyer2, vehicle, karaj,
                "پژو ۲۰۶ تیپ ۲ مدل ۱۳۹۸",
                "پژو ۲۰۶ تیپ ۲، مدل ۱۳۹۸، رنگ سفید، بیمه کامل، فنی و بدنه سالم.",
                new BigDecimal("650000000"), AdvertisementStatus.ACTIVE);

        favoriteRepository.save(new Favorite(buyer1, ad1));
        favoriteRepository.save(new Favorite(buyer1, ad3));
        favoriteRepository.save(new Favorite(buyer2, ad2));

        Conversation conversation = conversationRepository.save(new Conversation(buyer1, seller1, ad1));
        chatMessageRepository.save(new ChatMessage(conversation, buyer1,
                "سلام، آیا گوشی هنوز موجود است؟"));
        chatMessageRepository.save(new ChatMessage(conversation, seller1,
                "سلام، بله موجود است. می‌توانید امروز برای بازدید بیایید."));
        chatMessageRepository.save(new ChatMessage(conversation, buyer1,
                "بسیار خوب، فردا ساعت ۵ عصر می‌آیم."));

        Rating rating = new Rating();
        rating.setBuyer(buyer2);
        rating.setSeller(seller2);
        rating.setAdvertisement(ad4);
        rating.setScore(5);
        rating.setComment("فروشنده بسیار خوش‌برخورد بود و کالا دقیقا مطابق توضیحات بود.");
        ratingRepository.save(rating);
    }

    private User createUser(String fullName, String username, String rawPassword, String phoneNumber,
                             String email, Role role) {
        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Category createCategory(String title, String description, Category parent) {
        Category category = new Category(title, description);
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    private City createCity(String name) {
        return cityRepository.save(new City(name));
    }

    private Advertisement createAdvertisement(User owner, Category category, City city, String title,
                                                String description, BigDecimal price, AdvertisementStatus status) {
        Advertisement advertisement = new Advertisement();
        advertisement.setOwner(owner);
        advertisement.setCategory(category);
        advertisement.setCity(city);
        advertisement.setTitle(title);
        advertisement.setDescription(description);
        advertisement.setPrice(price);
        advertisement.setStatus(status);
        return advertisementRepository.save(advertisement);
    }
}
