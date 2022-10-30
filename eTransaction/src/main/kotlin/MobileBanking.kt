abstract class MobileBanking(nama: String, saldo: Long, noRekening: String) : DigitalPayment(nama, saldo) {
    val noRekening: String = ""

    var checkFree: Boolean = false

    val feeAntarBank : Long = 6000

    override fun transfer(dp: DigitalPayment, nominal: Long) : Unit {
        if (saldo < 0){
            println("pesan input tidak valid ")
        } else if (saldo <= 50000){
            println("Saldo tidak cukup")
        } else if(checkFree == true) {
            saldo = saldo - feeAntarBank
            saldo = saldo + nominal
            println("Transfer berhasil ${printBuktiTransfer(penerima = dp, nominal = nominal )}")
        }
    }
}