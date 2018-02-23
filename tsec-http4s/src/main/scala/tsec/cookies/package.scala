package tsec

import cats.Eq
import tsec.common._
import cats.evidence.Is
import cats.instances.string._
import tsec.cipher.symmetric._
import tsec.cipher.symmetric.core._
import tsec.cipher.symmetric.imports._
import tsec.mac.imports.MacVerificationError
import io.circe.{Decoder, Encoder, HCursor, Json}
import tsec.cipher.common.padding.NoPadding
import tsec.mac.core.{JCAMacTag, MAC}

package object cookies {

  protected val AEADCookie$$ : HKStringNewt = new HKStringNewt {
    type Repr[A] = String
    def is[A] = Is.refl[String]
  }

  type AEADCookie[A] = AEADCookie$$.Repr[A]

  sealed trait EVCookieEncrypt[F[_]] {
    def fromEncrypted[A: AES](a: CipherText[A], aad: AAD): F[A]

    def toString[A: AES](a: F[A]): String

    def subst[G[_], A: AES](fa: G[F[A]]): G[String]
  }

  implicit object AEADCookie extends EVCookieEncrypt[AEADCookie] {
    @inline def fromEncrypted[A: AES](a: CipherText[A], aad: AAD): AEADCookie[A] =
      AEADCookie$$.is[A].coerce(a.toSingleArray.toB64String + "-" + aad.toB64String)

    @inline def toString[A: AES](a: AEADCookie[A]): String = AEADCookie$$.is.coerce(a)

    @inline def subst[G[_], A: AES](fa: G[AEADCookie[A]]): G[String] = AEADCookie$$.is[A].flip.substitute[G](fa)

    @inline def apply[A: AES](raw: String): AEADCookie[A] = AEADCookie$$.is[A].coerce(raw)

    def getEncryptedContent[F[_], A: AES](
        signed: AEADCookie[A]
    )(implicit encryptor: AuthEncryptor[F, A, SecretKey]): Either[CipherTextError, CipherText[A]] = {
      val split = toString[A](signed).split("-")
      if (split.length != 2)
        Left(CipherTextError("String encoded improperly"))
      else {
        CTOPS.ciphertextFromArray[A, GCM, NoPadding](split(0).base64Bytes)
      }
    }

    implicit def circeDecoder[A: AES]: Decoder[AEADCookie[A]] = new Decoder[AEADCookie[A]] {
      def apply(c: HCursor) = c.as[String].map(AEADCookie.apply[A])
    }

    implicit def circeEncoder[A: AES]: Encoder[AEADCookie[A]] = new Encoder[AEADCookie[A]] {
      def apply(a: AEADCookie[A]): Json = Json.fromString(a)
    }

  }

  protected val SignedCookie$$ : HKStringNewt = new HKStringNewt {
    type Repr[A] = String

    def is[A] = Is.refl[Repr[A]]
  }

  type SignedCookie[A] = SignedCookie$$.Repr[A]

  sealed trait EVCookieMac[F[_]] {
    def from[A: JCAMacTag](a: MAC[A], joined: String): F[A]

    def apply[A: JCAMacTag](raw: String): F[A]

    def toString[A: JCAMacTag](a: F[A]): String
  }

  implicit object SignedCookie extends EVCookieMac[SignedCookie] {
    @inline def from[A: JCAMacTag](signed: MAC[A], joined: String): SignedCookie[A] =
      SignedCookie$$.is.coerce(joined + "-" + signed.toB64String)

    @inline def apply[A: JCAMacTag](raw: String): SignedCookie[A] = SignedCookie$$.is.coerce(raw)

    @inline def toString[A: JCAMacTag](a: SignedCookie[A]): String = SignedCookie$$.is.coerce(a)

    def getContent[A: JCAMacTag](signed: SignedCookie[A]): Either[MacVerificationError, String] = {
      val split = toString(signed).split("-")
      if (split.length != 2)
        Left(MacVerificationError("String encoded improperly"))
      else {
        fromDecodedString(split(0).base64Bytes.toUtf8String)
      }
    }

    def fromDecodedString(original: String): Either[MacVerificationError, String] =
      original.split("-") match {
        case Array(orig, nonce) =>
          Right(orig.base64Bytes.toUtf8String)
        case _ =>
          Left(MacVerificationError("String encoded improperly"))
      }

    @inline def is[A]: Is[String, SignedCookie[A]] = SignedCookie$$.is[A]
  }
  implicit final def cookieEQ[A: JCAMacTag]: Eq[SignedCookie[A]] = Eq.by[SignedCookie[A], String](identity[String])
  implicit final def ecookieEQ[A: AES]: Eq[AEADCookie[A]]        = Eq.by[AEADCookie[A], String](identity[String])
}
