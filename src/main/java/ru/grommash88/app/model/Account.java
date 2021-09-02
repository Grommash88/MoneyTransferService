package ru.grommash88.app.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.grommash88.app.model.enums.AccountStatus;
import ru.grommash88.app.model.enums.AccountType;
import ru.grommash88.app.model.enums.CurrencyType;

@Entity
@Table(name = "Accounts")
@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "class Account", description =
    "Сущность описывающая счет, на котором хранятся денежные средства.")
public class Account implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Уникальный id счета.", example = "100")
  private Long accId;

  @Enumerated(EnumType.STRING)
  @ApiModelProperty(value = "Статус счета.", example = "AccStatus.NORMAL")
  private AccountStatus accStatus;

  @Column(updatable = false)
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(value = "Тип счета.", example = "AccountType.DEBIT")
  private AccountType accountType;

  @Column(updatable = false)
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(value = "Валюта счета.", example = "CurrencyType.RUB")
  private CurrencyType accCurrency;

  @Column(updatable = false)
  @ApiModelProperty(value = "Номер счета.", example = "8100000100")
  private String accountNumber;

  @ApiModelProperty(value = "Баланс счета.", example = "50000.50")
  private volatile BigDecimal balance;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", updatable = false)
  @ApiModelProperty(
      value = "Держатель счета, ссылается на таблицу Users по полю user_id", example = "11")
  private User accHolder;

}
