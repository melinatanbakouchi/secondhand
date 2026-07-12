package ir.secondhand.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * تصویر مرتبط با یک آگهی. مسیر فایل به‌صورت نسبی ذخیره می‌شود.
 */
@Entity
@Table(name = "advertisement_images")
@Getter
@Setter
@NoArgsConstructor
public class AdvertisementImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String imagePath;

    @Column(nullable = false)
    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id", nullable = false)
    private Advertisement advertisement;

    public AdvertisementImage(String imagePath, int displayOrder) {
        this.imagePath = imagePath;
        this.displayOrder = displayOrder;
    }
}
