package scaladores.domain

import java.util.UUID

case class CreateAccountCommand(correlationUuid: UUID, document: String)
