
# Custom Response Format ile Exception Handling ve Controller Katmanı Düzenleme

Bu adımlarda, `RootEntity` ve `RestBaseController` sınıflarını kullanarak standart bir response yapısı oluşturacağız. Böylece API'dan dönen her bir veri, başarılı mı yoksa hata mı içerdiğini belirten yapılandırılmış bir response formatı kullanacaktır. Ayrıca, bu adımlar uygulama genelinde exception handling ve hata mesajlarının düzgün bir şekilde gösterilmesini sağlar.

## 1. **RootEntity Sınıfı**

`RootEntity`, API response'larında kullanılacak genel bir veri yapısıdır. Response, başarılı olduğunda veri (`data`), hata durumunda hata mesajı (`errorMessage`) içerir. Ayrıca, her response'un başarılı olup olmadığını belirten bir `result` alanı bulunmaktadır.

### RootEntity Yapısı:
```java
package com.example.model;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RootEntity<T> {

    private boolean result;
    private String errorMessage;
    private T data;

    public static <T> RootEntity<T> ok(T data) {
        RootEntity<T> rootEntity = new RootEntity<>();
        rootEntity.setData(data);
        rootEntity.setResult(true);
        rootEntity.setErrorMessage(null);
        return rootEntity;
    }

    public static <T> RootEntity<T> error(String errorMessage) {
        RootEntity<T> rootEntity = new RootEntity<>();
        rootEntity.setData(null);
        rootEntity.setErrorMessage(errorMessage);
        rootEntity.setResult(false);
        return rootEntity;
    }
}
```

### Açıklama:
- `ok()` metodu ile başarılı bir işlemde, data'nın dolu olduğu response döndürülür.
- `error()` metodu ise hata mesajıyla birlikte başarısız bir response döndürür.

## 2. **RestBaseController Sınıfı**

Bu sınıf, diğer controller sınıflarının kalıtım alarak `ok` ve `error` response'larını kullanmasına olanak sağlar. Bu şekilde, her controller'ın aynı response formatını kullanması garanti edilir.

### RestBaseController Yapısı:
```java
package com.example.controller.impl;

public class RestBaseController {

    public <T> RootEntity<T> ok(T data) {
        return RootEntity.ok(data);
    }

    public <T> RootEntity<T> error(String errorMessage) {
        return RootEntity.error(errorMessage);
    }
}
```

### Açıklama:
- `ok()` metodu, başarılı bir işlemden sonra response döndürür.
- `error()` metodu ise hata durumunda hata mesajını içerir şekilde response döndürür.

## 3. **RestEmployeeControllerImpl Sınıfı**

Bu sınıf, `RestBaseController` sınıfını kalıtım alarak employee verileri üzerinde işlemler yapar. Response formatı olarak `RootEntity` kullanılır.

### RestEmployeeControllerImpl Yapısı:
```java
package com.example.controller.impl;

@RestController
@RequestMapping("/rest/api/employee")
public class RestEmployeeControllerImpl extends RestBaseController implements IRestEmployeeController {

    @Autowired
    private IEmpolyeeService empolyeeService;

    @GetMapping("/list/{id}")
    @Override
    public RootEntity<DtoEmployee> getEmployeeById(@PathVariable(value = "id") Long id) {
        return ok(empolyeeService.getEmployeeById(id));
    }
}
```

### Açıklama:
- `getEmployeeById()` metodu, istenilen employee id ile birlikte veriyi döndürür. Eğer başarılı bir işlemse `RootEntity.ok()`, hata varsa `RootEntity.error()` kullanılır.
  
## Sonuç

Bu yapı ile tüm controller'lar aynı response yapısını kullanarak, tutarlı ve yapılandırılmış API response'ları oluşturur. Hatalar veya başarılı işlemler net bir şekilde ayrıştırılır ve kullanıcıya uygun formatta geri döndürülür.
