"""
Seed ChromaDB with 10 risk management domain knowledge documents.
This enables the RAG /query endpoint to return relevant context.
Run automatically on Flask startup via app.py.
"""
import logging
from services.chroma_client import ingest_document, collection

logger = logging.getLogger(__name__)

KNOWLEDGE_DOCS = {
    "iso_31000": """
ISO 31000 Risk Management Framework provides guidelines for managing risk faced by organizations.
Key principles include: risk management creates and protects value, is an integral part of all
organizational processes, is part of decision making, explicitly addresses uncertainty, is systematic
structured and timely, is based on the best available information, is tailored, takes human and
cultural factors into account, is transparent and inclusive, is dynamic iterative and responsive
to change, and facilitates continual improvement of the organization. The risk management process
involves establishing context, risk assessment (identification, analysis, evaluation), risk treatment,
monitoring and review, and communication and consultation.
""",

    "nist_rmf": """
The NIST Risk Management Framework (RMF) provides a structured process for integrating security,
privacy, and cyber supply chain risk management into the system development life cycle. The six
steps are: Categorize the information system, Select security controls, Implement security controls,
Assess security controls, Authorize the information system, and Monitor security controls. NIST
SP 800-37 Rev 2 emphasizes continuous monitoring and near real-time risk management with ongoing
authorization. Risk is calculated as the product of threat likelihood and impact severity.
""",

    "cybersecurity_risks": """
Common cybersecurity risks include: ransomware attacks that encrypt data and demand payment,
phishing attacks that trick users into revealing credentials, SQL injection attacks against web
applications, cross-site scripting (XSS) attacks, denial of service (DDoS) attacks that overwhelm
systems, insider threats from employees with access, zero-day vulnerabilities in unpatched software,
man-in-the-middle attacks on network communications, supply chain attacks through third-party
vendors, and advanced persistent threats (APTs) by nation-state actors. Mitigation strategies
include defense in depth, regular patching, security awareness training, network segmentation,
incident response planning, and continuous monitoring.
""",

    "financial_risk": """
Financial risk management involves identifying, measuring, and managing risks related to financial
operations. Key types include: market risk (changes in market prices, interest rates, currency
exchange rates), credit risk (counterparty default), liquidity risk (inability to meet short-term
obligations), operational risk (failures in processes, people, systems), and systemic risk
(collapse of entire financial systems). Common metrics include Value at Risk (VaR), Expected
Shortfall, stress testing, and sensitivity analysis. Basel III framework requires banks to
maintain minimum capital ratios and liquidity coverage ratios to absorb losses.
""",

    "operational_risk": """
Operational risk is the risk of loss resulting from inadequate or failed internal processes,
people, systems, or external events. Categories include: process failures (errors in execution,
inadequate controls), people risks (fraud, human error, key person dependency, staff turnover),
system failures (IT outages, software bugs, data corruption), external events (natural disasters,
regulatory changes, pandemics). The Basel II framework classifies operational risk events into
seven categories: internal fraud, external fraud, employment practices, clients products and
business practices, damage to physical assets, business disruption, and execution delivery.
Key indicators include loss event frequency, severity distribution, and near-miss reporting.
""",

    "compliance_risk": """
Compliance risk refers to the potential for legal penalties, financial forfeiture, or material
loss when organizations fail to act in accordance with industry laws and regulations. Key
regulations include: GDPR (data protection in EU), SOX (financial reporting in US), PCI DSS
(payment card security), HIPAA (healthcare data protection), ISO 27001 (information security
management). Compliance programs should include: written policies and procedures, designated
compliance officer, training and education, effective lines of communication, monitoring and
auditing, disciplinary guidelines, and prompt response to detected offenses.
""",

    "business_continuity": """
Business Continuity Planning (BCP) ensures critical business functions continue during and
after a disaster. Key components include: Business Impact Analysis (BIA) to identify critical
processes and their recovery priorities, Recovery Time Objective (RTO) specifying maximum
acceptable downtime, Recovery Point Objective (RPO) specifying maximum acceptable data loss,
disaster recovery site planning (hot, warm, cold sites), communication plans for stakeholders,
regular testing and exercising of plans, and supply chain resilience assessment. ISO 22301
provides the international standard for business continuity management systems.
""",

    "risk_assessment_methods": """
Common risk assessment methodologies include: qualitative methods (risk matrices, expert judgment,
Delphi technique), quantitative methods (Monte Carlo simulation, fault tree analysis, event
tree analysis), semi-quantitative methods (scoring systems with numerical ratings). Risk scoring
typically uses a matrix of likelihood (rare, unlikely, possible, likely, almost certain) and
impact (insignificant, minor, moderate, major, catastrophic) to derive a risk level. The risk
register is the central tool for documenting identified risks, their assessment, treatment plans,
and residual risk levels. Regular risk reviews should be conducted quarterly or when significant
changes occur.
""",

    "supply_chain_risk": """
Supply chain risk management (SCRM) addresses vulnerabilities across the end-to-end supply chain.
Key risks include: supplier dependency (single source of supply), geopolitical disruptions (trade
wars, sanctions), logistics failures (port congestion, transportation disruption), quality risks
(defective components, contamination), demand volatility (bullwhip effect), cyber risks (attacks
on supplier systems), regulatory compliance across jurisdictions, and natural disaster impacts.
Mitigation strategies include supplier diversification, safety stock buffers, near-shoring,
supply chain visibility platforms, and contractual risk transfer through insurance and indemnity
clauses.
""",

    "emerging_technology_risk": """
Emerging technology risks include: artificial intelligence bias and fairness concerns, deepfake
technology for social engineering, quantum computing threats to current encryption, Internet of
Things (IoT) security vulnerabilities, cloud computing shared responsibility gaps, blockchain
smart contract vulnerabilities, autonomous vehicle liability questions, 5G network security
challenges, and edge computing data sovereignty issues. Organizations should maintain a technology
risk radar, conduct regular horizon scanning, and integrate emerging technology risk assessment
into their overall risk management framework. The pace of technological change means risk
assessments must be updated more frequently than traditional annual reviews.
"""
}


def seed_chromadb():
    """Seed ChromaDB with domain knowledge documents if not already seeded."""
    try:
        existing_count = collection.count()
        if existing_count > 0:
            logger.info(f"ChromaDB already has {existing_count} documents. Skipping seed.")
            return

        logger.info("Seeding ChromaDB with risk management knowledge base...")
        for doc_id, text in KNOWLEDGE_DOCS.items():
            ingest_document(doc_id, text.strip())
            logger.info(f"  Ingested: {doc_id}")

        final_count = collection.count()
        logger.info(f"ChromaDB seeding complete. Total documents: {final_count}")

    except Exception as e:
        logger.error(f"ChromaDB seeding failed: {e}")


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    seed_chromadb()
