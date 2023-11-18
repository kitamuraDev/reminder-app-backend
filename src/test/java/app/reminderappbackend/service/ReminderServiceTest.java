package app.reminderappbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reminderapi.model.ReminderForm;

@SpringBootTest
public class ReminderServiceTest {

  @Autowired
  private ReminderService service;

  @Nested
  class method_of_findById {
    @Test
    void 指定のIDに紐づくリソースが取得できるか() {
      Long verifyId = 1L; // テスト対象ID
      ReminderEntity expectedEntity = createExpectedEntity();

      ReminderEntity actualEntity = service.findById(verifyId);

      assertThat(actualEntity.getId()).isEqualTo(expectedEntity.getId());
      assertThat(actualEntity.getTitle()).isEqualTo(expectedEntity.getTitle());
      assertThat(actualEntity.getDescription()).isEqualTo(expectedEntity.getDescription());
      assertThat(actualEntity.getDueDate()).isEqualTo(expectedEntity.getDueDate());
      assertThat(actualEntity.getPriority()).isEqualTo(expectedEntity.getPriority());
      assertThat(actualEntity.getIsCompleted()).isEqualTo(expectedEntity.getIsCompleted());
      assertThat(actualEntity.getCreatedAt().withSecond(0).withNano(0)).isEqualTo(expectedEntity.getCreatedAt().withSecond(0).withNano(0));
      assertThat(actualEntity.getUpdatedAt().withSecond(0).withNano(0)).isEqualTo(expectedEntity.getUpdatedAt().withSecond(0).withNano(0));
    }

    @Test
    void 存在しないIDを指定したときにReminderEntityNotFoundExceptionを投げるか() {
      Long verifyId = 99L;

      assertThatThrownBy(() -> service.findById(verifyId))
        .isInstanceOf(ReminderEntityNotFoundException.class);
    }
  }

  @Nested
  class method_of_findList {
    Integer defaultLimit = 10;
    Long defaultOffset = 0L;

    @Test
    void 指定範囲のリソースをリストで取得できるか() {
      // Integer expectedListSize = 3; // このテストを単体で実行する場合はサイズは3
      Integer expectedListSize = 5; // 全てのテストを一斉実行する場合、`create`の箇所で2回insertされるためサイズが5になる

      List<ReminderEntity> actualEntityList = service.findList(defaultLimit, defaultOffset);

      assertThat(actualEntityList)
        .isNotNull().as("Nullではないはず")
        .hasSize(expectedListSize).as("テストデータは3件のはず");
    }

    @Test
    void 各フィールドのNullチェック() {
      List<ReminderEntity> actualEntityList = service.findList(defaultLimit, defaultOffset);

      assertThat(actualEntityList)
        .allSatisfy(actualEntity -> {
          assertThat(actualEntity.getId()).isNotNull();
          assertThat(actualEntity.getTitle()).isNotNull();
          assertThat(actualEntity.getDescription()).isNotNull();
          assertThat(actualEntity.getDueDate()).isNotNull();
          assertThat(actualEntity.getPriority()).isNotNull();
          assertThat(actualEntity.getIsCompleted()).isNotNull();
          assertThat(actualEntity.getCreatedAt()).isNotNull();
          assertThat(actualEntity.getUpdatedAt()).isNotNull();
        });
    }

    @Test
    void 指定範囲にリソースが存在しない場合リストは空か() {
      Integer limit = 0;
      Long offset = 0L;

      List<ReminderEntity> actualEntityList = service.findList(limit, offset);

      assertThat(actualEntityList)
        .as("limitが0であれば、Listのサイズは0であるはず").hasSize(0)
        .as("limitが0であれば、Listは空であるはず").isEmpty();
    }

  }

  @Nested
  class method_of_create {
    @Test
    void レコードを登録できるか() {
      ReminderForm form = createForm();
      ReminderEntity actualEntity = service.create(form);

      // MyBatisの`@Insert`の仕様上、idは戻り値に取れないため、ここではidの検証は実施しないこととする
      assertThat(actualEntity.getTitle()).isNotNull();
      assertThat(actualEntity.getDescription()).isNotNull();
      assertThat(actualEntity.getDescription()).isNotNull();
      assertThat(actualEntity.getDueDate()).isNotNull();
      assertThat(actualEntity.getPriority()).isNotNull();
      assertThat(actualEntity.getIsCompleted()).isNotNull();
      assertThat(actualEntity.getCreatedAt()).isNotNull();
      assertThat(actualEntity.getUpdatedAt()).isNotNull();
    }

    @Test
    void formとentityのフィールドが一致しているか() {
      ReminderForm form = createForm();
      ReminderEntity actualEntity = service.create(form);

      assertThat(actualEntity.getTitle()).isEqualTo(form.getTitle());
      assertThat(actualEntity.getDescription()).isEqualTo(form.getDescription());
      assertThat(actualEntity.getDueDate()).isEqualTo(form.getDueDate());
      assertThat(actualEntity.getPriority()).isEqualTo(form.getPriority());
      assertThat(actualEntity.getIsCompleted()).isEqualTo(form.getIsCompleted());
    }
  }

  @Nested
  class method_of_update {
    @Test
    void IDが存在しない場合ReminderEntityNotFoundExceptionを投げるか() {
      Long verifyId = 99L;
      ReminderForm verifyForm = createForm();

      assertThatThrownBy(() -> service.update(verifyId, verifyForm))
        .isInstanceOf(ReminderEntityNotFoundException.class);
    }
  }

  @Nested
  class method_of_delete {
    @Test
    void IDが存在しない場合ReminderEntityNotFoundExceptionを投げるか() {
      Long verifyId = 99L;

      assertThatThrownBy(() -> service.delete(verifyId))
        .isInstanceOf(ReminderEntityNotFoundException.class);
    }
  }

  public ReminderEntity createExpectedEntity() {
    return new ReminderEntity(
      1L,
      "カレーのルーを購入する",
      "夕飯がカレーなのでカレーのルーを買います",
      LocalDate.of(2023, 9, 14),
      1,
      false,
      OffsetDateTime.now(ZoneOffset.UTC).plusHours(9), // 実測値のタイムゾーン（UTC）に合わせる
      OffsetDateTime.now(ZoneOffset.UTC).plusHours(9)  // 実測値のタイムゾーン（UTC）に合わせる
    );
  }

  public ReminderForm createForm() {
    return new ReminderForm(
      "Hello.",
      "Hello SpringBoot App.",
      LocalDate.of(2023, 10, 26),
      1,
      false
    );
  }

}
