package app.reminderappbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.reminderappbackend.repository.ReminderRecord;
import app.reminderappbackend.repository.ReminderRepository;
import reminderapi.model.ReminderForm;

// モックを初期化するためのアノテーション
@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    // モッククラス
    // `@InjectMocks`が付いたクラスに注入される
    @Mock
    private ReminderRepository reminderRepository;

    // テスト対象クラス
    // `@InjectMocks`が付いたクラスが依存するクラスのモック（`@Mock`が付いたクラス）が注入される
    @InjectMocks
    private ReminderService reminderService;

    @Nested
    class method_of_findById {
      @Test
      void 指定のIDに紐づくリソースが取得できるか() {
        Long verifyId = 1L; // テスト対象ID
        ReminderRecord expectedRecord = createExpectedRecord();
        ReminderEntity expectedEntity = createExpectedEntity();

        when(reminderRepository.selectById(verifyId)).thenReturn(Optional.of(expectedRecord)); // reminderRepository.selectById() の振る舞いを定義
        ReminderEntity actualEntity = reminderService.findById(verifyId);

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
        when(reminderRepository.selectById(verifyId)).thenReturn(Optional.empty());

        assertThrows(ReminderEntityNotFoundException.class, () -> {
          reminderService.findById(verifyId);
        });
      }
    }

    @Nested
    class method_of_findList {
      Integer defaultLimit = 10;
      Long defaultOffset = 0L;

      @Test
      void 指定範囲のリソースをリストで取得できるか() {
        Integer expectedListSize = 3; // テストデータの総数
        List<ReminderRecord> expectedRecordList = createExpectedRecordList();

        when(reminderRepository.selectList(defaultLimit, defaultOffset)).thenReturn(expectedRecordList);
        List<ReminderEntity> actualEntityList = reminderService.findList(defaultLimit, defaultOffset);

        assertNotNull(actualEntityList, "Nullではないはず");
        assertEquals(expectedListSize, actualEntityList.size(), "テストデータは3件のはず");
      }

      @Test
      void 各フィールドのNullチェック() {
        List<ReminderRecord> expectedRecordList = createExpectedRecordList();

        when(reminderRepository.selectList(defaultLimit, defaultOffset)).thenReturn(expectedRecordList);
        List<ReminderEntity> actualEntityList = reminderService.findList(defaultLimit, defaultOffset);

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
        List<ReminderRecord> expectedRecordList = Collections.emptyList();

        when(reminderRepository.selectList(limit, offset)).thenReturn(expectedRecordList);
        List<ReminderEntity> actualEntityList = reminderService.findList(limit, offset);

        assertTrue(actualEntityList.isEmpty(), "limitが0であれば、Listは空であるはず");
        assertEquals(0, actualEntityList.size(), "limitが0であれば、Listのサイズは0であるはず");
      }
    }

    @Nested
    class method_of_create {
      @Test
      void レコードを登録できるか() {
        ReminderForm form = createRegisterForm();
        ReminderEntity actualEntity = reminderService.create(form);

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
        ReminderForm form = createRegisterForm();
        ReminderEntity actualEntity = reminderService.create(form);

        assertEquals(form.getTitle(), actualEntity.getTitle());
        assertEquals(form.getDescription(), actualEntity.getDescription());
        assertEquals(form.getDueDate(), actualEntity.getDueDate());
        assertEquals(form.getPriority(), actualEntity.getPriority());
        assertEquals(form.getIsCompleted(), actualEntity.getIsCompleted());
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

    private ReminderEntity createExpectedEntity() {
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

    private List<ReminderRecord> createExpectedRecordList() {
      ReminderRecord record_1 = new ReminderRecord(1L, "カレーのルーを購入する", "夕飯がカレーなのでカレーのルーを買います", LocalDate.of(2023, 9, 14), 1, false, LocalDateTime.now(), LocalDateTime.now());
      ReminderRecord record_2 = new ReminderRecord(2L, "ランニング", "毎日の日課のランニング。今日は5km走りました", LocalDate.of(2023, 9, 15), 2, true, LocalDateTime.now(), LocalDateTime.now());
      ReminderRecord record_3 = new ReminderRecord(3L, "開発環境のセットアップ", "今晩からリマインダーアプリの開発を行うので、開発環境のセットアップを行います", LocalDate.of(2023, 9, 16), 0, false, LocalDateTime.now(), LocalDateTime.now());

      List<ReminderRecord> list = new ArrayList<ReminderRecord>();
      list.add(record_1);
      list.add(record_2);
      list.add(record_3);

      return list;
    }

    private ReminderForm createRegisterForm() {
      return new ReminderForm(
        "Hello.",
        "Hello SpringBoot App.",
        LocalDate.of(2023, 10, 26),
        1,
        false
      );
    }
}
