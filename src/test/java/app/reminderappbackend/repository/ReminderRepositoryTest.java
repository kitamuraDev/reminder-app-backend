package app.reminderappbackend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
      ReminderRecord actualRecord = actualOptRecord.get();
      ReminderRecord expectedRecord = createExpectedRecord();

      assertThat(actualRecord.getId()).isEqualTo(expectedRecord.getId());
      assertThat(actualRecord.getTitle()).isEqualTo(expectedRecord.getTitle());
      assertThat(actualRecord.getDescription()).isEqualTo(expectedRecord.getDescription());
      assertThat(actualRecord.getDueDate()).isEqualTo(expectedRecord.getDueDate());
      assertThat(actualRecord.getPriority()).isEqualTo(expectedRecord.getPriority());
      assertThat(actualRecord.isCompleted()).isEqualTo(expectedRecord.isCompleted());

      // created_at,updated_at は LocalDateTime型のため、テストを実行する際に秒以下（秒とナノ秒）を0に合わせないとテストが通らない（※テストにおいて秒以下の値比較は重要ではないと判断）
      // そのため、以下のように `withSecond()`と`withNano()` メソッドで秒以下を0に合わせている
      assertThat(actualRecord.getCreatedAt().withSecond(0).withNano(0)).isEqualTo(expectedRecord.getCreatedAt().withSecond(0).withNano(0));
      assertThat(actualRecord.getUpdatedAt().withSecond(0).withNano(0)).isEqualTo(expectedRecord.getUpdatedAt().withSecond(0).withNano(0));
    }

    @Test
    void 存在しないIDを指定したときにOptionalが空であるか() {
      Optional<ReminderRecord> actualOptRecord = reminderRepository.selectById(99L);

      assertThat(actualOptRecord)
        .isEmpty().as("IDが存在しない場合、Optionalは空であるべき");
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

      assertThat(actualRecordList)
        .isNotNull().as("Nullではないはず")
        .hasSize(expectedListSize).as("テストデータは3件のはず");
    }

    @Test
    void 各フィールドのNullチェック() {
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, defaultOffset);

      assertThat(actualRecordList)
        .allSatisfy(actualRecord -> {
          assertThat(actualRecord.getId()).isNotNull();
          assertThat(actualRecord.getTitle()).isNotNull();
          assertThat(actualRecord.getDescription()).isNotNull();
          assertThat(actualRecord.getDueDate()).isNotNull();
          assertThat(actualRecord.getPriority()).isNotNull();
          assertThat(actualRecord.isCompleted()).isNotNull();
          assertThat(actualRecord.getCreatedAt()).isNotNull();
          assertThat(actualRecord.getUpdatedAt()).isNotNull();
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void limitが機能するか_正常系(Integer limit) {
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(limit, defaultOffset);

      assertThat(actualRecordList)
        .isNotEmpty().as("limitが0より大きければ、Listは空ではないはず")
        .hasSize(limit).as("limitが0より大きければ、Listのサイズはlimitと同値であるはず");
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void limitが機能するか_異常系(Integer limit) {
      Integer expectedListSize = 0;
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(limit, defaultOffset);

      assertThat(actualRecordList)
        .as("limitが0であれば、Listのサイズは0であるはず").hasSize(expectedListSize)
        .as("limitが0であれば、Listは空であるはず").isEmpty();
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 2})
    void offsetが機能するか_正常系(Long offset) {
      Integer expectedListSize = 3; // テストデータの総数
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, offset);

      assertThat(actualRecordList)
        .isNotEmpty().as("offsetがデータ総数より小さければ、Listは空ではないはず")
        .hasSize(Math.toIntExact(expectedListSize - offset)).as("Listのサイズは「テストデータの総数 - offset」であるはず");
    }

    @ParameterizedTest
    @ValueSource(longs = {3, 4, 5})
    void offsetが機能するか_異常系(Long offset) {
      Integer expectedListSize = 0;
      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, offset);

      assertThat(actualRecordList)
        .as("offsetがデータ総数より大きければ、Listのサイズは0であるはず").hasSize(expectedListSize)
        .as("offsetがデータ総数より大きければ、Listは空であるはず").isEmpty();
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

      assertThat(actualRecordList)
        .hasSize(expectedListSize).as("Listのサイズは一致するはず");
    }
  }

  @Nested
  class method_of_insert {
    Integer defaultLimit = 10;
    Long defaultOffset = 0L;

    @Test
    void レコードを登録できるか() {
      List<ReminderRecord> beforeList = reminderRepository.selectList(defaultLimit, defaultOffset);
      Long expectedLastIndexId = beforeList.get(beforeList.size() - 1).getId() + 1; // insert前の最終要素のid

      ReminderRecord record = createRegisterRecord();
      reminderRepository.insert(record);

      List<ReminderRecord> afterList = reminderRepository.selectList(defaultLimit, defaultOffset);
      Integer actualListSize = afterList.size();

      assertThat(afterList)
        .hasSize(actualListSize).as("レコードの登録が成功していれば、Listのサイズは一致するはず")
        .last()
        .extracting(ReminderRecord::getId)
        .isEqualTo(expectedLastIndexId).as("DB内でidの自動採番ができていれば、idは一致するはず");
    }

    @Test
    void 登録したレコードのNullチェック() {
      ReminderRecord record = createRegisterRecord();
      reminderRepository.insert(record);

      List<ReminderRecord> actualRecordList = reminderRepository.selectList(defaultLimit, defaultOffset);
      Long addedRecordId = Long.valueOf(actualRecordList.size() - 1); // last index
      ReminderRecord actualRecord = reminderRepository.selectById(addedRecordId).get();

      assertThat(actualRecord.getId()).isNotNull();
      assertThat(actualRecord.getTitle()).isNotNull();
      assertThat(actualRecord.getDescription()).isNotNull();
      assertThat(actualRecord.getDueDate()).isNotNull();
      assertThat(actualRecord.getPriority()).isNotNull();
      assertThat(actualRecord.isCompleted()).isNotNull();
      assertThat(actualRecord.getCreatedAt()).isNotNull();
      assertThat(actualRecord.getUpdatedAt()).isNotNull();
    }

    @Test
    void 登録するレコードにnullが含まれる場合PersistenceExceptionを投げるか() {
      ReminderRecord record = createHasNullRecord();

      assertThatThrownBy(() -> reminderRepository.insert(record))
        .isInstanceOf(PersistenceException.class);
    }
  }

  @Nested
  class method_of_update {
    Long defaultId = 1L;
    ReminderForm defaultForm = createForm();

    @Test
    void レコードを更新できるか() {
      reminderRepository.update(defaultId, defaultForm);

      ReminderRecord actualRecord = reminderRepository.selectById(defaultId).get();

      assertThat(actualRecord.getTitle()).isEqualTo(defaultForm.getTitle());
      assertThat(actualRecord.getDescription()).isEqualTo(defaultForm.getDescription());
      assertThat(actualRecord.getDueDate()).isEqualTo(defaultForm.getDueDate());
      assertThat(actualRecord.getPriority()).isEqualTo(defaultForm.getPriority());
      assertThat(actualRecord.isCompleted()).isEqualTo(defaultForm.getIsCompleted());
    }

    @Test
    void 更新したレコードのNullチェック() {
      reminderRepository.update(defaultId, defaultForm);

      ReminderRecord actualRecord = reminderRepository.selectById(defaultId).get();

      assertThat(actualRecord.getId()).isNotNull();
      assertThat(actualRecord.getTitle()).isNotNull();
      assertThat(actualRecord.getDescription()).isNotNull();
      assertThat(actualRecord.getDueDate()).isNotNull();
      assertThat(actualRecord.getPriority()).isNotNull();
      assertThat(actualRecord.isCompleted()).isNotNull();
      assertThat(actualRecord.getCreatedAt()).isNotNull();
      assertThat(actualRecord.getUpdatedAt()).isNotNull();
    }

    @Test
    void 更新するレコードにnullが含まれる場合PersistenceExceptionを投げるか() {
      ReminderForm form = createHasNullForm();

      assertThatThrownBy(() -> reminderRepository.update(defaultId, form))
        .isInstanceOf(PersistenceException.class);
    }
  }

  @Nested
  class method_of_delete {
    Long defaultId = 1L;

    @Test
    void レコードを削除できるか() {
      reminderRepository.delete(defaultId);

      assertThatThrownBy(() -> reminderRepository.selectById(defaultId).get())
        .isInstanceOf(NoSuchElementException.class)
        .as("レコードの削除が成功していれば、存在しないレコードにアクセスすることになり、NoSuchElementExceptionが発生するはず");
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
