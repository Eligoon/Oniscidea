
package app.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class GoogleTokenMigration {

    public static void migrate(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            List<Object[]> rows = em.createNativeQuery(
                    "SELECT id, accesstoken, refreshtoken " +
                            "FROM googlecalendarconnection"
            ).getResultList();

            for (Object[] row : rows) {
                Integer id = ((Number) row[0]).intValue();
                String accessToken = (String) row[1];
                String refreshToken = (String) row[2];

                String encryptedAccessToken =
                        GoogleTokenEncryption.encrypt(accessToken);
                String encryptedRefreshToken =
                        GoogleTokenEncryption.encrypt(refreshToken);

                if (!encryptedAccessToken.equals(accessToken)
                        || !encryptedRefreshToken.equals(refreshToken)) {

                    jakarta.persistence.Query query = em.createNativeQuery(
                            "UPDATE googlecalendarconnection " +
                                    "SET accesstoken = ?1, refreshtoken = ?2 " +
                                    "WHERE id = ?3"
                    );

                    query.setParameter(1, encryptedAccessToken);
                    query.setParameter(2, encryptedRefreshToken);
                    query.setParameter(3, id);
                    query.executeUpdate();

                }
            }

            transaction.commit();

        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
