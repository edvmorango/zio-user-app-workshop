package scaladores.service.pure

import cats.implicits._
import scaladores.domain.CreateAccountCommand
import scaladores.failure.AccountFailure
import scaladores.failure.AccountFailure.AccountValidationFailure

object AccountFunctions {

  def validateDocumentSize(c: CreateAccountCommand): Either[AccountFailure, CreateAccountCommand] = {
    if (c.document.length == 11)
      c.asRight
    else
      AccountValidationFailure("document", "invalid size").asLeft

  }

  def validateDocumentCharacters(c: CreateAccountCommand): Either[AccountFailure, CreateAccountCommand] = {
    if (c.document.forall(_.isDigit))
      c.asRight
    else
      AccountValidationFailure("document", "invalid characters").asLeft
  }

}
