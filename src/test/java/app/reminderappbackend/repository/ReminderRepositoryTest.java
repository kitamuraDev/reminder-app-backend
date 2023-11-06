package app.reminderappbackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import reminderapi.model.ReminderForm;

@MybatisTest
public class ReminderRepositoryTest {

  @Autowired
  private SqlSessionFactory sqlSessionFactory;

  private ReminderRepository reminderRepository;

  @BeforeEach
  void setUp() {
    // MyBatis の SqlSession にマッパーを登録した情報を reminderRepository に代入
    reminderRepository = sqlSessionFactory.openSession().getMapper(ReminderRepository.class);
  }

  @Nested
  class method_of_selectById {
    @Test
    void 指定のIDに紐づくリソースが取得できるか() {
      Optional<ReminderRecord> actualOptRecord = reminderRepository.selectById(1L);
      ReminderRecord expectedRecord = createExpectedRecord();

      assertEquals(expectedRecord.getId(), actualOptRecord.get().getId());
      assertEquals(expectedRecord.getTitle(), actualOptRecord.get().getTitle());
      assertEquals(expectedRecord.getDescription(), actualOptRecord.get().getDescription());
      assertEquals(expectedRecord.getDueDate(), actualOptRecord.get().getDueDate());
      assertEquals(expectedRecord.getPriority(), actualOptRecord.get().getPriority());
      assertEquals(expectedRecord.isCompleted(), actualOptRecord.get().isCompleted());

      // created_at,updated_at は LocalDateTime型のため、テストを実行する際に秒以下（秒とナノ秒）を0に合わせないとテストが通らない（※テストにおいて秒以下の値比較は重要ではないと判断）
      // そのため、以下のように `withSecond()`と`withNano()` メソッドで秒以下を0に合わせている
      assertEquals(expectedRecord.getCreatedAt().withSecond(0).withNano(0), actualOptRecord.get().getCreatedAt().withSecond(0).withNano(0));
      assertEquals(expectedRecord.getUpdatedAt().withSecond(0).withNano(0), actualOptRecord.get().getUpdatedAt().withSecond(0).withNano(0));
    }

    @Test
    void 存在しないIDを指定したときにOptionalが空であるか() {
      Optional<ReminderRecord> actualOptRecord = reminderRepository.selectById(99L);

      assertFalse(actualOptRecord.isPresent(), "Optionalは空であるべき");
    }
  }

  @Nested
  class method_of_selectList {
    Integer defaultLimit = 10;
    Long defaultOffset = 0L;

    @Test
    void 指定範囲のリソースをリストで取得できるか() {
      Integer expectedListSize = 3; // テストデータの総数
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, defaultOffset);

      assertNotNull(actualRecordList, "Nullではないはず");
      assertEquals(expectedListSize, actualRecordList.size(), "テストデータは3件のはず");
    }

    @Test
    void 各フィールドのNullチェック() {
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, defaultOffset);

      actualRecordList.forEach(actualRecord -> {
        assertNotNull(actualRecord.getId());
        assertNotNull(actualRecord.getTitle());
        assertNotNull(actualRecord.getDescription());
        assertNotNull(actualRecord.getDueDate());
        assertNotNull(actualRecord.getPriority());
        assertNotNull(actualRecord.isCompleted());
        assertNotNull(actualRecord.getCreatedAt());
        assertNotNull(actualRecord.getUpdatedAt());
      });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void limitが機能するか_正常系(Integer limit) {
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(limit, defaultOffset);

      assertFalse(actualRecordList.isEmpty(), "limitが0より大きければ、Listは空ではないはず");
      assertEquals(limit, actualRecordList.size(), "limitが0より大きければ、Listのサイズはlimitと同値であるはず");
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void limitが機能するか_異常系(Integer limit) {
      Integer expectedListSize = 0;
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(limit, defaultOffset);

      assertTrue(actualRecordList.isEmpty(), "limitが0であれば、Listは空であるはず");
      assertEquals(expectedListSize, actualRecordList.size(), "limitが0であれば、Listのサイズは0であるはず");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 2})
    void offsetが機能するか_正常系(Long offset) {
      Integer expectedListSize = 3; // テストデータの総数
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, offset);

      assertFalse(actualRecordList.isEmpty(), "offsetがデータ総数より小さければ、Listは空ではないはず");
      assertEquals(expectedListSize - offset, actualRecordList.size(), "Listのサイズは「テストデータの総数 - offset」であるはず");
    }

    @ParameterizedTest
    @ValueSource(longs = {3, 4, 5})
    void offsetが機能するか_異常系(Long offset) {
      Integer expectedListSize = 0;
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, offset);

      assertTrue(actualRecordList.isEmpty(), "offsetがデータ総数より大きければ、Listは空であるはず");
      assertEquals(expectedListSize, actualRecordList.size(), "offsetがデータ総数より大きければ、Listのサイズは0であるはず");
    }

    @ParameterizedTest
    @CsvSource({
      "1, 0",
      "2, 1",
      "3, 2",
    })
    void limitとoffsetの組み合わせが機能するか(Integer limit, Long offset) {
      Integer totalsize = 3; // テストデータの総数
      Integer expectedListSize = Math.min(limit, totalsize - offset.intValue());
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(limit, offset);

      assertEquals(expectedListSize, actualRecordList.size(), "Listのサイズは一致するはず");
    }
  }

  @Nested
  class method_of_insert {
    Integer defaultLimit = 10;
    Long defaultOffset = 0L;

    @Test
    void レコードを登録できるか() {
      List<ReminderRecord> beforeList = reminderRepository.selectList(defaultLimit, defaultOffset);
      Integer expectedListSize = beforeList.size() + 1;
      Long expectedLastIndexId = beforeList.get(beforeList.size() - 1).getId() + 1; // insert前の最終要素のid

      ReminderRecord record = createRegisterRecord();
      reminderRepository.insert(record);

      List<ReminderRecord> afterList = reminderRepository.selectList(defaultLimit, defaultOffset);
      Integer actualListSize = afterList.size();
      Long actualLastIndexId = afterList.get(afterList.size() - 1).getId(); // insert後の最終要素のid

      assertEquals(expectedListSize, actualListSize, "レコードの登録が成功していれば、Listのサイズは一致するはず");
      assertEquals(expectedLastIndexId, actualLastIndexId, "DB内でidの自動採番ができていれば、idは一致するはず");
    }

    @Test
    void 登録したレコードのNullチェック() {
      ReminderRecord record = createRegisterRecord();
      reminderRepository.insert(record);

      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, defaultOffset);
      Long addedRecordId = Long.valueOf(actualRecordList.size() - 1); // last index
      Optional<ReminderRecord> actualRecord = reminderRepository.selectById(addedRecordId);

      assertNotNull(actualRecord.get().getId());
      assertNotNull(actualRecord.get().getTitle());
      assertNotNull(actualRecord.get().getDescription());
      assertNotNull(actualRecord.get().getDueDate());
      assertNotNull(actualRecord.get().getPriority());
      assertNotNull(actualRecord.get().isCompleted());
      assertNotNull(actualRecord.get().getCreatedAt());
      assertNotNull(actualRecord.get().getUpdatedAt());
    }

    @Test
    void 登録するレコードにnullが含まれる場合PersistenceExceptionを投げるか() {
      ReminderRecord record = createHasNullRecord();

      assertThrows(PersistenceException.class, () -> {
        reminderRepository.insert(record);
      });
    }
  }

  @Nested
  class method_of_update {
    Long defaultId = 1L;
    ReminderForm defaultForm = createForm();

    @Test
    void レコードを更新できるか() {
      reminderRepository.update(defaultId, defaultForm);

      Optional<ReminderRecord> actualOptRecord = reminderRepository.selectById(defaultId);

      assertEquals(defaultForm.getTitle(), actualOptRecord.get().getTitle());
      assertEquals(defaultForm.getDescription(), actualOptRecord.get().getDescription());
      assertEquals(defaultForm.getDueDate(), actualOptRecord.get().getDueDate());
      assertEquals(defaultForm.getPriority(), actualOptRecord.get().getPriority());
      assertEquals(defaultForm.getIsCompleted(), actualOptRecord.get().isCompleted());
    }

    @Test
    void 更新したレコードのNullチェック() {
      reminderRepository.update(defaultId, defaultForm);

      Optional<ReminderRecord> actualOptRecord = reminderRepository.selectById(defaultId);

      assertNotNull(actualOptRecord.get().getId());
      assertNotNull(actualOptRecord.get().getTitle());
      assertNotNull(actualOptRecord.get().getDescription());
      assertNotNull(actualOptRecord.get().getDueDate());
      assertNotNull(actualOptRecord.get().getPriority());
      assertNotNull(actualOptRecord.get().isCompleted());
      assertNotNull(actualOptRecord.get().getCreatedAt());
      assertNotNull(actualOptRecord.get().getUpdatedAt());
    }

    @Test
    void 更新するレコードにnullが含まれる場合PersistenceExceptionを投げるか() {
      ReminderForm form = createHasNullForm();

      assertThrows(PersistenceException.class, () -> {
        reminderRepository.update(defaultId, form);
      });
    }
  }

  @Nested
  class method_of_delete {
    Long defaultId = 1L;

    @Test
    void レコードを削除できるか() {
      reminderRepository.delete(defaultId);

      assertThrows(NoSuchElementException.class, () -> {
        Optional<ReminderRecord> actualOptRecord = reminderRepository.selectById(defaultId);
        actualOptRecord.get();
      }, "レコードの削除が成功していれば、存在しないレコードにアクセスすることになり、NoSuchElementExceptionが発生するはず");
    }
  }

  private ReminderRecord createExpectedRecord() {
    return new ReminderRecord(
      1L,
      "カレーのルーを購入する",
      "夕飯がカレーなのでカレーのルーを買います",
      LocalDate.of(2023, 9, 14),
      1,
      false,
      LocalDateTime.now(),
      LocalDateTime.now()
    );
  }

  private ReminderRecord createRegisterRecord() {
    return new ReminderRecord(
      null,
      "Hello.",
      "Hello SpringBoot App.",
      LocalDate.of(2023, 10, 26),
      1,
      false,
      LocalDateTime.now(),
      LocalDateTime.now()
    );
  }

  private ReminderRecord createHasNullRecord() {
    return new ReminderRecord(
      null,
      null,
      null,
      LocalDate.of(2023, 10, 26),
      1,
      false,
      LocalDateTime.now(),
      LocalDateTime.now()
    );
  }

  private ReminderForm createForm() {
    return new ReminderForm(
      "edited title.",
      "edited description.",
      LocalDate.now(),
      1,
      false
    );
  }

  private ReminderForm createHasNullForm() {
    return new ReminderForm(
      null,
      null,
      LocalDate.now(),
      1,
      false
    );
  }
}
