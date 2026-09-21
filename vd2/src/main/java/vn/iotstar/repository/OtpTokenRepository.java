package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.OtpPurpose;
import vn.iotstar.entity.OtpToken;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

	Optional<OtpToken> findFirstByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(String email, OtpPurpose purpose);

	void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
