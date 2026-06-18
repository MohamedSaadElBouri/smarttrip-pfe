package ma.groupe07.tourisme.modules.file.repository;

import ma.groupe07.tourisme.modules.file.model.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedImageRepository extends JpaRepository<UploadedImage, Long> {
}
