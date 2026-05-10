package com.risk.risk_simulator.config;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.entity.User;
import com.risk.risk_simulator.repository.RiskScenarioRepository;
import com.risk.risk_simulator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Seeds 30 realistic demo records + 3 demo users on startup if DB is empty.
 */
@Configuration
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedData(RiskScenarioRepository riskRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed demo users if they don't exist
            if (!userRepo.existsByUsername("admin")) {
                logger.info("Seeding demo users...");
                userRepo.saveAll(Arrays.asList(
                    User.builder().username("admin").email("admin@risksim.com")
                        .password(passwordEncoder.encode("admin123")).role("ADMIN").isActive(true).build(),
                    User.builder().username("manager").email("manager@risksim.com")
                        .password(passwordEncoder.encode("manager123")).role("MANAGER").isActive(true).build(),
                    User.builder().username("viewer").email("viewer@risksim.com")
                        .password(passwordEncoder.encode("viewer123")).role("VIEWER").isActive(true).build()
                ));
                logger.info("✅ Seeded 3 demo users (admin/admin123, manager/manager123, viewer/viewer123)");
            }

            // Seed risk scenarios if empty
            if (riskRepo.count() == 0) {
                logger.info("Seeding 30 demo risk scenarios...");
                List<RiskScenario> risks = Arrays.asList(
                    risk("Regional Data Center Power Failure", "Primary data center in US-East experiencing intermittent power supply issues due to aging UPS infrastructure. Risk of cascading failures across dependent microservices.", "Infrastructure", 8.5, "CRITICAL", "HIGH", "MEDIUM", "OPEN", "Deploy redundant UPS systems and implement automatic failover to US-West region within 30 seconds.", 250000L),
                    risk("SQL Injection in Legacy API Gateway", "Penetration testing revealed SQL injection vulnerability in the v1 authentication endpoint. Attacker could extract user credentials and session tokens.", "Cybersecurity", 9.2, "CRITICAL", "HIGH", "HIGH", "CRITICAL", "Implement parameterized queries, deploy WAF rules, and schedule emergency patch for v1 API gateway.", 85000L),
                    risk("Currency Exchange Rate Volatility", "Exposure to EUR/USD fluctuations impacting Q2 revenue projections by approximately 4.2%. Hedging strategy needs immediate review.", "Financial", 6.8, "HIGH", "HIGH", "MEDIUM", "IN_PROGRESS", "Engage treasury team to implement forward contracts covering 80% of projected EUR exposure for Q2-Q3.", 180000L),
                    risk("Supply Chain Semiconductor Shortage", "Critical chip shortage affecting production of IoT sensor modules. Lead times extended from 8 to 22 weeks.", "Operational", 7.5, "HIGH", "HIGH", "HIGH", "OPEN", "Diversify supplier base across 3 regions. Negotiate priority allocation with existing vendors.", 420000L),
                    risk("GDPR Data Retention Non-Compliance", "Audit revealed customer PII retained beyond 36-month regulatory limit in 3 legacy databases. Potential fine up to 4% of annual revenue.", "Compliance", 8.0, "CRITICAL", "CRITICAL", "MEDIUM", "IN_PROGRESS", "Implement automated data purge pipeline. Deploy retention policy enforcement across all databases by Q2.", 150000L),
                    risk("Market Entry Strategy - APAC Region", "Competitive analysis shows 3 new entrants in APAC market. Current market share at risk of 15% erosion over next 2 quarters.", "Strategic", 5.5, "MEDIUM", "MEDIUM", "MEDIUM", "OPEN", "Accelerate APAC partnership program. Launch localized product variants for JP and KR markets.", 600000L),
                    risk("Kubernetes Cluster Auto-Scaling Failure", "Production K8s cluster failed to auto-scale during Black Friday load test. Peak traffic caused 12-second response times.", "Infrastructure", 7.8, "HIGH", "HIGH", "MEDIUM", "MITIGATED", "Reconfigure HPA thresholds, add cluster autoscaler with pre-warming, implement load shedding.", 95000L),
                    risk("Ransomware Attack Vector via Email", "Phishing simulation showed 23% of employees clicked malicious links. Ransomware could encrypt critical business systems.", "Cybersecurity", 8.8, "CRITICAL", "CRITICAL", "HIGH", "OPEN", "Mandatory security awareness training, deploy advanced email filtering, implement network segmentation.", 200000L),
                    risk("Interest Rate Hike Impact on Credit Portfolio", "Projected 75bp rate increase will affect credit portfolio. Expected increase in default rates by 1.8%.", "Financial", 6.2, "HIGH", "HIGH", "LOW", "IN_PROGRESS", "Stress test portfolio under multiple rate scenarios. Adjust risk appetite framework accordingly.", 340000L),
                    risk("Warehouse Automation System Downtime", "Robotic picking system averaging 4.2 hours unplanned downtime per week. Fulfillment SLAs at risk of breach.", "Operational", 5.8, "MEDIUM", "MEDIUM", "HIGH", "MITIGATED", "Implement predictive maintenance schedule. Deploy backup manual picking stations.", 175000L),
                    risk("SOC 2 Type II Audit Gap", "Internal pre-audit identified 7 control gaps in access management and change control processes.", "Compliance", 7.0, "HIGH", "HIGH", "MEDIUM", "IN_PROGRESS", "Remediate access control gaps. Implement automated change management workflow with approval chains.", 120000L),
                    risk("Product-Market Fit Drift in Enterprise Segment", "NPS scores dropped from 72 to 54 in enterprise segment over 6 months. Feature gap analysis shows 12 unmet requirements.", "Strategic", 6.5, "HIGH", "HIGH", "MEDIUM", "OPEN", "Form enterprise advisory board. Prioritize top 5 feature requests in next 2 sprint cycles.", 280000L),
                    risk("DNS Infrastructure Single Point of Failure", "All DNS resolution routes through single provider. Provider outage would render all services unreachable.", "Infrastructure", 8.2, "CRITICAL", "CRITICAL", "LOW", "MITIGATED", "Implement multi-provider DNS with automated failover. Add health checks with 30-second TTL.", 45000L),
                    risk("API Key Exposure in Public Repository", "Automated scanning detected 3 API keys committed to public GitHub repository. Keys provide access to payment processing sandbox.", "Cybersecurity", 7.2, "HIGH", "HIGH", "MEDIUM", "CLOSED", "Rotated all exposed keys. Implemented pre-commit hooks and secret scanning in CI/CD pipeline.", 25000L),
                    risk("Revenue Recognition Policy Change Impact", "New ASC 606 interpretation affects SaaS revenue timing. Potential revenue deferral in Q3.", "Financial", 5.0, "MEDIUM", "MEDIUM", "LOW", "OPEN", "Engage external auditors for interpretation review. Model financial impact across 3 scenarios.", 90000L),
                    risk("Third-Party Logistics Provider Insolvency", "Primary 3PL provider showing financial distress signals. Handles 40% of outbound shipments.", "Operational", 6.0, "HIGH", "HIGH", "LOW", "OPEN", "Identify and onboard backup 3PL provider. Negotiate volume commitments to reduce dependency.", 310000L),
                    risk("PCI DSS v4.0 Migration Deadline", "New PCI DSS requirements effective Q3. Current payment infrastructure requires 14 configuration changes.", "Compliance", 7.5, "HIGH", "HIGH", "MEDIUM", "IN_PROGRESS", "Create PCI migration project plan. Engage QSA for gap assessment and remediation roadmap.", 200000L),
                    risk("Talent Acquisition Pipeline Bottleneck", "Engineering hiring velocity dropped 40%. Average time-to-fill increased from 45 to 78 days.", "Strategic", 4.5, "MEDIUM", "MEDIUM", "HIGH", "IN_PROGRESS", "Expand recruiter team. Launch employee referral bonus program. Partner with 3 coding bootcamps.", 150000L),
                    risk("Cloud Storage Encryption Key Rotation Failure", "Automated key rotation failed silently for 47 days. 2.3TB of data encrypted with expired keys.", "Infrastructure", 8.0, "HIGH", "HIGH", "LOW", "CLOSED", "Fixed rotation automation. Implemented monitoring alerts for key lifecycle events.", 55000L),
                    risk("Zero-Day Vulnerability in TLS Library", "CVE-2026-XXXX affects OpenSSL version used in all production services. CVSS score 9.1.", "Cybersecurity", 9.5, "CRITICAL", "CRITICAL", "HIGH", "CRITICAL", "Emergency patching cycle initiated. WAF rules deployed as interim mitigation. 24/7 SOC monitoring.", 130000L),
                    risk("Insurance Premium Escalation", "Cyber insurance premiums projected to increase 35% at renewal. Coverage gaps identified in cloud infrastructure.", "Financial", 4.0, "MEDIUM", "MEDIUM", "MEDIUM", "OPEN", "Broker market review for competitive quotes. Improve security posture to qualify for premium discounts.", 275000L),
                    risk("Manufacturing Quality Control Deviation", "Batch testing revealed 2.1% defect rate exceeding 1.5% threshold. Root cause traced to calibration drift.", "Operational", 5.5, "MEDIUM", "MEDIUM", "MEDIUM", "MITIGATED", "Implemented daily calibration checks. Deployed statistical process control with automated alerts.", 88000L),
                    risk("Environmental Reporting Deadline Risk", "ESG reporting requirements deadline in 60 days. Data collection from 3 subsidiaries incomplete.", "Compliance", 6.8, "HIGH", "HIGH", "MEDIUM", "OPEN", "Assign dedicated ESG data analyst per subsidiary. Weekly progress reviews with compliance officer.", 65000L),
                    risk("Digital Transformation ROI Underperformance", "Year 1 digital transformation ROI at 12% vs projected 25%. User adoption rates below expectations.", "Strategic", 5.0, "MEDIUM", "MEDIUM", "HIGH", "IN_PROGRESS", "Launch change management program. Implement gamified training and executive sponsorship roadshow.", 450000L),
                    risk("CDN Cache Poisoning Vulnerability", "Security assessment identified potential cache poisoning vector in CDN configuration. Could serve malicious content.", "Cybersecurity", 7.8, "HIGH", "HIGH", "LOW", "MITIGATED", "Implemented cache key normalization. Added origin verification headers and cache integrity monitoring.", 40000L),
                    risk("Database Replication Lag Exceeding SLA", "Primary-replica lag averaging 4.2 seconds during peak hours. Read-after-write consistency broken for users.", "Infrastructure", 6.5, "MEDIUM", "MEDIUM", "HIGH", "OPEN", "Upgrade replica instance class. Implement connection routing to primary for critical read paths.", 72000L),
                    risk("Vendor Lock-in Risk with Cloud Provider", "85% of infrastructure on single cloud provider. Migration cost estimated at significant amount if terms change.", "Strategic", 5.2, "HIGH", "HIGH", "LOW", "OPEN", "Adopt multi-cloud strategy for new workloads. Containerize top 10 services for portability.", 520000L),
                    risk("Customer Data Export Request Backlog", "DSAR request queue at 340 pending. Average fulfillment time exceeding 30-day regulatory requirement.", "Compliance", 7.2, "HIGH", "HIGH", "HIGH", "CRITICAL", "Deploy automated DSAR fulfillment tool. Hire 2 temporary data privacy analysts for backlog clearance.", 95000L),
                    risk("Accounts Receivable Aging Deterioration", "AR over 90 days increased from 8% to 14%. Top 5 accounts represent significant overdue payments.", "Financial", 6.0, "MEDIUM", "MEDIUM", "MEDIUM", "IN_PROGRESS", "Implement automated collection escalation workflow. Engage collections agency for accounts over 120 days.", 110000L),
                    risk("Employee Burnout and Attrition Spike", "Engineering team voluntary attrition at 22% annualized. Exit interviews cite workload and lack of growth opportunities.", "Operational", 7.0, "HIGH", "HIGH", "HIGH", "OPEN", "Implement mandatory PTO policy. Launch mentorship program and internal mobility platform.", 380000L)
                );
                riskRepo.saveAll(risks);
                logger.info("✅ Seeded 30 demo risk scenarios covering all statuses and categories");
            }
        };
    }

    private RiskScenario risk(String title, String desc, String category, double score,
                              String riskLevel, String impact, String likelihood,
                              String status, String mitigation, Long cost) {
        return RiskScenario.builder()
                .title(title)
                .description(desc)
                .category(category)
                .riskScore(score)
                .riskLevel(riskLevel)
                .impact(impact)
                .likelihood(likelihood)
                .status(status)
                .mitigationPlan(mitigation)
                .projectedCost(cost)
                .deleted(false)
                .build();
    }
}
