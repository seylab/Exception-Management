
# Exception Handling

Spring Boot projelerinde hataları daha anlaşılır bir şekilde fırlatmak ve yönetmek için özel istisnalar (exceptions) kullanılabilir. Aşağıda, `BaseException`, `ErrorMessage`, ve `MessageType` kullanarak nasıl exception handling yapılabileceği anlatılmıştır.

## 1. BaseException

`BaseException` sınıfı, uygulama içinde fırlatılacak özel bir istisnadır ve `RuntimeException` sınıfından türemektedir.

```java
package com.example.exceptions;

public class BaseException extends RuntimeException {
    public BaseException() {
    }

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
```

- **BaseException:** İki constructor içerir. İlki boş, ikincisi ise `ErrorMessage` objesi alır ve üst sınıfa fırlatılacak hatayı iletir.
- **RuntimeException'dan Türeme:** Bu, çalışma zamanında fırlatılacak özel bir istisnadır.

## 2. ErrorMessage

`ErrorMessage` sınıfı, bir hata mesajını dinamik olarak oluşturmak için kullanılır.

```java
package com.example.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private MessageType messageType;
    private String ofStatic;

    public String prepareErrorMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(messageType.getMessage());
        if (ofStatic != null) {
            builder.append(" : ").append(ofStatic);
        }
        return builder.toString();
    }
}
```

- **messageType:** Bu, hata türünü temsil eder (`MessageType` enum'undan gelir).
- **ofStatic:** Hata mesajına eklenebilecek statik bilgileri içerir.
- **prepareErrorMessage():** Hata mesajını birleştirip döner. Mesajın içine `messageType`'ın mesajını ve statik bilgiyi ekler.

## 3. MessageType Enum

`MessageType`, farklı hata mesajlarını yönetmek için kullanılan bir enum’dur.

```java
package com.example.exceptions;

import lombok.Getter;

@Getter
public enum MessageType {
    NO_RECORD_EXIST("1001", "Record Not Found"),
    GENERAL_EXCEPTION("9999", "Genel bir hata oluştu");

    private String code;
    private String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

- **NO_RECORD_EXIST:** "1001" koduyla, "Record Not Found" mesajını içerir.
- **GENERAL_EXCEPTION:** Genel bir hata için kullanılan mesaj ve kod.

## 4. Exception Handling Uygulama Örneği

Bir serviste, belirli bir çalışan kaydını ararken kaydın bulunamaması durumunda bu exception nasıl fırlatılabilir?

```java
@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public DtoEmployee getEmployeeById(Long id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }

        Employee employee = optional.get();
        // Employee'yi Dto'ya dönüştürüp döndürme işlemi yapılır.
    }
}
```

- `getEmployeeById()` metodu, çalışanın ID’sine göre bir kayıt arar.
- Eğer kayıt bulunamazsa, `BaseException` fırlatılır ve `ErrorMessage` ile hatanın mesajı detaylandırılır.

## 5. Kullanım Örnekleri

### 5.1. Exception Kullanımı

Eğer bir kaydın varlığı sorgulanırken kayıt bulunamazsa:

```java
throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
```

Bu durumda, aşağıdaki mesaj fırlatılacaktır:

```
Record Not Found : [ID]
```

### 5.2. Global Exception Handling (Opsiyonel)

Projede `@ControllerAdvice` ve `@ExceptionHandler` kullanarak global exception handling yapılabilir. Bu sayede, proje genelindeki istisnalar merkezi bir şekilde yönetilebilir.

---

Bu notlar, özel exception sınıfları ile uygulamada nasıl hata fırlatabileceğinizi ve bu hataların mesajlarını nasıl oluşturabileceğinizi anlatmaktadır.


## Exception Handling - GlobalExceptionHandler ve ApiError Yapısı

Bu çalışmada, özel bir istisna (exception) yönetim mekanizması oluşturmak için `handler` paketine ait bazı sınıflar ekledik.
Aşağıda, sınıfların nasıl çalıştığı ve uygulama genelinde istisna yönetiminin nasıl ele alındığı detaylandırılmıştır.

### 1. ``GlobalExceptionHandler`` Sınıfı

Bu sınıf `@ControllerAdvice` anotasyonu ile tanımlanmıştır. 
Bu anotasyon, sınıfı uygulama genelindeki istisnaları yakalayıp yönetebilen bir sınıf haline getirir. 
Belirtilen `@ExceptionHandler` metodu, yakalanan istisnaları ele alır.

Spring uygulamalarında, hata yönetimi için merkezi bir yapı oluşturmak amacıyla 
`GlobalExceptionHandler` sınıfı oluşturulmuştur. 
Bu sınıf, uygulama genelinde meydana gelen özel hataları ele almak için kullanılır.

```java
package com.example.handler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError> handleBaseException(BaseException exception, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), request));
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("Failed" + e.getMessage());
        }
        return null;
    }

    public <E> ApiError<E> createApiError(E message, WebRequest request) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());

        Exception<E> exception = new Exception<>();
        exception.setCreateTime(new Date());
        exception.setHostName(getHostname());
        exception.setPath(request.getDescription(false).substring(4));
        exception.setMessage(message);
        apiError.setException(exception);

        return apiError;
    }
}
```

#### Açıklama
* ``@ControllerAdvice:`` Uygulamanın genelinde hata yönetimi yapabilmek için kullanılır.
* ``@ExceptionHandler:`` Belirtilen istisnalar için özel bir yöntem tanımlar.
* ``handleBaseException:`` ``BaseException`` türündeki hataları ele alır ve bir ``ApiError`` döndürür.
* ``createApiError:`` Hata bilgilerini içeren bir ``ApiError`` nesnesi oluşturur.


### 2. ``ApiError`` Sınıfı
``ApiError`` sınıfı, oluşan hatanın detaylarını API yanıtı olarak döndürmek için kullanılır. İçerisinde hata durumu (``status``) ve hata ile ilgili ayrıntıları barındıran ``Exception`` nesnesini tutar.

````java
package com.example.handler;

@Getter
@Setter
public class ApiError<E> {

    private Integer status;

    private Exception<E> exception;
}
````
#### Açıklama
* ``ApiError:`` HTTP yanıtında döndürülecek hata bilgilerini tutar. Hata durumu ve istisna bilgilerini içerir.
* ``status:`` HTTP hata durum kodunu içerir.
* ``exception:`` Hata detaylarını içeren ``Exception`` nesnesini tutar.

### 3. ``Exception`` Sınıfı

``Exception`` sınıfı, hatanın ayrıntılarını tutar. Hangi host'ta, hangi path üzerinde, hangi zaman
diliminde hatanın oluştuğunu ve hatanın mesajını içeren alanlara sahiptir.
````java
package com.example.handler;

@Getter
@Setter
public class Exception<E> {

    private String hostName;

    private String path;

    private Date createTime;

    private E message;
}
````

#### Açıklama
* ``Exception:`` Hata ile ilgili ayrıntıları tutar. İsim, yol, oluşturulma zamanı ve hata mesajı gibi bilgiler içerir.
* ``hostName:`` Hatayı oluşturan sunucunun hostname bilgisini tutar. 
* ``path:`` Hataya neden olan URL yolunu tutar. 
* ``createTime:`` Hatanın oluştuğu zaman dilimini içerir. 
* ``message:`` Hataya ilişkin mesajı tutar.
 
#### Genel Yapı
  Bu exception handling yapısında:

**1.** Hata meydana geldiğinde, ``GlobalExceptionHandler`` devreye girer.

**2.** Hata ``BaseException`` türünde ise, hata mesajı ve detayları `ApiError` ve `Exception` sınıfları aracılığıyla yapılandırılır.

**3.** API yanıtı olarak HTTP 400 Bad Request döndürülür ve hata detayı yanıt gövdesine eklenir.
