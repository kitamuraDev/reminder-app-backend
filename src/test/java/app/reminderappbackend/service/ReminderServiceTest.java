package app.reminderappbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

      assertEquals(expectedEntity.getId(), actualEntity.getId());
      assertEquals(expectedEntity.getTitle(), actualEntity.getTitle());
      assertEquals(expectedEntity.getDescription(), actualEntity.getDescription());
      assertEquals(expectedEntity.getDueDate(), actualEntity.getDueDate());
      assertEquals(expectedEntity.getPriority(), actualEntity.getPriority());
      assertEquals(expectedEntity.getIsCompleted(), actualEntity.getIsCompleted());
      assertEquals(expectedEntity.getCreatedAt().withSecond(0).withNano(0), actualEntity.getCreatedAt().withSecond(0).withNano(0));
      assertEquals(expectedEntity.getUpdatedAt().withSecond(0).withNano(0), actualEntity.getUpdatedAt().withSecond(0).withNano(0));
    }

    @Test
    void 存在しないIDを指定したときにReminderEntityNotFoundExceptionを投げるか() {
      Long verifyId = 99L;

      assertThrows(ReminderEntityNotFoundException.class, () -> {
        service.findById(verifyId);
      });
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

      assertNotNull(actualEntityList, "Nullではないはず");
      assertEquals(expectedListSize, actualEntityList.size(), "テストデータは3件のはず");
    }

    @Test
    void 各フィールドのNullチェック() {
      List<ReminderEntity> actualEntityList = service.findList(defaultLimit, defaultOffset);

      actualEntityList.forEach(actualEntity -> {
        assertNotNull(actualEntity.getId());
        assertNotNull(actualEntity.getTitle());
        assertNotNull(actualEntity.getDescription());
        assertNotNull(actualEntity.getDueDate());
        assertNotNull(actualEntity.getPriority());
        assertNotNull(actualEntity.getIsCompleted());
        assertNotNull(actualEntity.getCreatedAt());
        assertNotNull(actualEntity.getUpdatedAt());
      });
    }

    @Test
    void 指定範囲にリソースが存在しない場合リストは空か() {
      Integer limit = 0;
      Long offset = 0L;

      List<ReminderEntity> actualEntityList = service.findList(limit, offset);

      assertTrue(actualEntityList.isEmpty(), "limitが0であれば、Listは空であるはず");
      assertEquals(0, actualEntityList.size(), "limitが0であれば、Listのサイズは0であるはず");
    }

  }

  @Nested
  class method_of_create {
    @Test
    void レコードを登録できるか() {
      ReminderForm form = createForm();
      ReminderEntity actualEntity = service.create(form);

      // MyBatisの`@Insert`の仕様上、idは戻り値に取れないため、ここではidの検証は実施しないこととする
      assertNotNull(actualEntity.getTitle());
      assertNotNull(actualEntity.getDescription());
      assertNotNull(actualEntity.getDueDate());
      assertNotNull(actualEntity.getPriority());
      assertNotNull(actualEntity.getIsCompleted());
      assertNotNull(actualEntity.getCreatedAt());
      assertNotNull(actualEntity.getUpdatedAt());
    }

    @Test
    void formとentityのフィールドが一致しているか() {
      ReminderForm form = createForm();
      ReminderEntity actualEntity = service.create(form);

      assertEquals(form.getTitle(), actualEntity.getTitle());
      assertEquals(form.getDescription(), actualEntity.getDescription());
      assertEquals(form.getDueDate(), actualEntity.getDueDate());
      assertEquals(form.getPriority(), actualEntity.getPriority());
      assertEquals(form.getIsCompleted(), actualEntity.getIsCompleted());
    }
  }

  @Nested
  class method_of_update {
    @Test
    void IDが存在しない場合ReminderEntityNotFoundExceptionを投げるか() {
      Long verifyId = 99L;
      ReminderForm verifyForm = createForm();

      assertThrows(ReminderEntityNotFoundException.class, () -> {
        service.update(verifyId, verifyForm);
      });
    }
  }

  @Nested
  class method_of_delete {
    @Test
    void IDが存在しない場合ReminderEntityNotFoundExceptionを投げるか() {
      Long verifyId = 99L;

      assertThrows(ReminderEntityNotFoundException.class, () -> {
        service.delete(verifyId);
      });
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
