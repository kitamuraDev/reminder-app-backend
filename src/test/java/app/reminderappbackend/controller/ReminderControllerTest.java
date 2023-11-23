package app.reminderappbackend.controller;

import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import reminderapi.model.ReminderForm;

@SpringBootTest
@AutoConfigureMockMvc
public class ReminderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  class method_of_getReminder {
    @Test
    void 指定のIDに紐づくリソースが取得できるか() throws Exception {
      Long verifyId = 1L;

      mockMvc.perform(MockMvcRequestBuilders.get("/reminders/{id}", verifyId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(verifyId))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("カレーのルーを購入する"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("夕飯がカレーなのでカレーのルーを買います"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value("2023-09-14"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.isCompleted").value("false"));
    }

    @Test
    void 存在しないIDを指定したときに404を投げるか() throws Exception {
      Long verifyId = 99L;

      mockMvc.perform(MockMvcRequestBuilders.get("/reminders/{id}", verifyId))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("ReminderEntity (id = " + verifyId + ") is not found."));
    }
  }

  @Nested
  class method_of_getReminderList {
    @Test
    void 指定範囲のリソースをリストで取得できるか() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get("/reminders/")
        .param("limit", "10")
        .param("offset", "0"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(3))
      .andExpect(MockMvcResultMatchers.jsonPath("$.results").isNotEmpty());
    }

    @Test
    void 不正なリクエストを行った場合400のエラーレスポンスを返すか() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get("/reminders/")
        .param("limit", "0")
        .param("offset", "0"))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("リクエストが不正です。正しいリクエストでリトライしてください"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params[0].name").value("limit"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params[0].reason").value("must be greater than or equal to 1"));
    }
  }

  @Nested
  class method_of_createReminder {
    @Test
    void リソースを作成できるか() throws Exception {
      ReminderForm form = createNormalForm();

      mockMvc.perform(MockMvcRequestBuilders.post("/reminders/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.notNullValue()))
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Hello."))
      .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Hello SpringBoot App."))
      .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value("2023-10-26"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value("1"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.isCompleted").value("false"));
    }

    // ※本来であれば、バリデーションエラーのパターン数分テストしなければならない
    @Test
    void 不正なリクエストを行った場合400のエラーレスポンスを返すか() throws Exception {
      ReminderForm form = createHasNullForm();

      mockMvc.perform(MockMvcRequestBuilders.post("/reminders/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("リクエストが不正です。正しいリクエストでリトライしてください"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params[0].name").value("title"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params[0].reason").value("must not be null"));
    }
  }

  @Nested
  class method_of_updateReminder {
    @Test
    void リソースを更新できるか() throws Exception {
      Long verifyId = 1L;
      ReminderForm form = createNormalForm();

      mockMvc.perform(MockMvcRequestBuilders.put("/reminders/{id}", verifyId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(verifyId))
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Hello."))
      .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Hello SpringBoot App."))
      .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value("2023-10-26"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value("1"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.isCompleted").value("false"));
    }

    @Test
    void 不正なリクエストを行った場合400のエラーレスポンスを返すか() throws Exception {
      Long verifyId = 1L;
      ReminderForm form = createHasNullForm();

      mockMvc.perform(MockMvcRequestBuilders.put("/reminders/{id}", verifyId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("リクエストが不正です。正しいリクエストでリトライしてください"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params[0].name").value("title"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params[0].reason").value("must not be null"));
    }

    @Test
    void 存在しないIDを指定したときに404を投げるか() throws Exception {
      Long verifyId = 99L;
      ReminderForm form = createNormalForm();

      mockMvc.perform(MockMvcRequestBuilders.put("/reminders/{id}", verifyId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource Not Found"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("ReminderEntity (id = " + verifyId + ") is not found."));
    }
  }

  @Nested
  class method_of_deleteReminder {
    @Test
    void リソースを削除できるか() throws Exception {
      Long verifyId = 1L;

      mockMvc.perform(MockMvcRequestBuilders.delete("/reminders/{id}", verifyId))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void 存在しないIDを指定したときに404を投げるか() throws Exception {
      Long verifyId = 99L;

      mockMvc.perform(MockMvcRequestBuilders.delete("/reminders/{id}", verifyId))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("ReminderEntity (id = " + verifyId + ") is not found."));
    }
  }

  public ReminderForm createNormalForm() {
    return new ReminderForm(
      "Hello.",
      "Hello SpringBoot App.",
      LocalDate.of(2023, 10, 26),
      1,
      false
    );
  }

  public ReminderForm createHasNullForm() {
    return new ReminderForm(
      null,
      "Hello SpringBoot App.",
      LocalDate.of(2023, 10, 26),
      1,
      false
    );
  }
}
